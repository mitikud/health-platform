import { useEffect, useLayoutEffect, useRef, useState } from "react";

type Props = {
  onStop: (file: File) => void;
  height?: number;
  timesliceMs?: number;
  className?: string;
  onError?: (err: unknown) => void;
};

export default function RecorderWave({
  onStop,
  height = 80,
  timesliceMs = 1000,
  className,
  onError,
}: Props) {
  const [recording, setRecording] = useState(false);
  const [supported, setSupported] = useState(false);
  const [errMsg, setErrMsg] = useState<string | null>(null);

  const mediaRecRef = useRef<MediaRecorder | null>(null);
  const mediaStreamRef = useRef<MediaStream | null>(null);
  const chunksRef = useRef<BlobPart[]>([]);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const rafRef = useRef<number | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const audioCtxRef = useRef<AudioContext | null>(null);
  const sourceRef = useRef<MediaStreamAudioSourceNode | null>(null);
  const resizeObsRef = useRef<ResizeObserver | null>(null);

  const pickMimeType = () => {
    const cands = [
      "audio/webm;codecs=opus",
      "audio/webm",
      "audio/mp4;codecs=mp4a.40.2",
      "audio/mp4",
    ];
    const isSup = (window as any).MediaRecorder?.isTypeSupported?.bind(
      (window as any).MediaRecorder
    );
    return cands.find((t) => isSup?.(t));
  };

  const setCanvasSize = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const dpr = Math.max(1, window.devicePixelRatio || 1);
    const cssW = canvas.clientWidth || 0;
    canvas.width = Math.max(1, Math.floor(cssW * dpr));
    canvas.height = Math.max(1, Math.floor(height * dpr));
  };

  const stopVisual = () => {
    if (rafRef.current) cancelAnimationFrame(rafRef.current);
    rafRef.current = null;
    try {
      sourceRef.current?.disconnect();
      analyserRef.current?.disconnect();
    } catch {}
    try {
      audioCtxRef.current?.close();
    } catch {}
    analyserRef.current = null;
    sourceRef.current = null;
    audioCtxRef.current = null;
  };

  const draw = () => {
    const analyser = analyserRef.current;
    const canvas = canvasRef.current;
    if (!analyser || !canvas) return;

    const ctx = canvas.getContext("2d")!;
    const bufferLength = analyser.fftSize;
    const data = new Uint8Array(bufferLength);

    const render = () => {
      analyser.getByteTimeDomainData(data);
      const { width, height: h } = canvas;

      ctx.clearRect(0, 0, width, h);
      ctx.fillStyle = "#ffffff";
      ctx.fillRect(0, 0, width, h);

      ctx.lineWidth = 2;
      ctx.strokeStyle = "#111827";
      ctx.beginPath();
      const slice = width / bufferLength;
      let x = 0;
      for (let i = 0; i < bufferLength; i++) {
        const v = data[i] / 128.0;
        const y = (v * h) / 2;
        i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
        x += slice;
      }
      ctx.lineTo(width, h / 2);
      ctx.stroke();

      rafRef.current = requestAnimationFrame(render);
    };
    rafRef.current = requestAnimationFrame(render);
  };

  useEffect(() => {
    const ok =
      !!navigator.mediaDevices?.getUserMedia &&
      "MediaRecorder" in window &&
      (window.AudioContext || (window as any).webkitAudioContext) &&
      (window.isSecureContext || location.hostname === "localhost");
    setSupported(ok);

    return () => {
      try {
        if (mediaRecRef.current && mediaRecRef.current.state !== "inactive") {
          mediaRecRef.current.stop();
        }
      } catch {}
      mediaStreamRef.current?.getTracks().forEach((t) => t.stop());
      stopVisual();
      resizeObsRef.current?.disconnect();
    };
  }, []);

  useLayoutEffect(() => {
    setCanvasSize();
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ro = new ResizeObserver(() => setCanvasSize());
    ro.observe(canvas);
    resizeObsRef.current = ro;
    return () => ro.disconnect();
  }, [height]);

  async function start() {
    if (!supported || recording) return;
    setErrMsg(null);

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaStreamRef.current = stream;

      const mimeType = pickMimeType();
      const rec = mimeType ? new MediaRecorder(stream, { mimeType }) : new MediaRecorder(stream);
      mediaRecRef.current = rec;

      chunksRef.current = [];
      rec.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) chunksRef.current.push(e.data);
      };
      rec.onstop = () => {
        stream.getTracks().forEach((t) => t.stop());
        const type = rec.mimeType || mimeType || "audio/webm";
        const blob = new Blob(chunksRef.current, { type });
        const ext = type.includes("mp4") ? "m4a" : "webm";
        const file = new File([blob], `recording-${Date.now()}.${ext}`, { type });
        onStop(file);
        stopVisual();
      };

      const AC: typeof AudioContext =
        (window as any).AudioContext || (window as any).webkitAudioContext;
      const actx = new AC();
      audioCtxRef.current = actx;

      const source = actx.createMediaStreamSource(stream);
      sourceRef.current = source;

      const analyser = actx.createAnalyser();
      analyser.fftSize = 2048;
      analyser.smoothingTimeConstant = 0.85;
      analyserRef.current = analyser;
      source.connect(analyser);

      setCanvasSize();
      draw();

      rec.start(timesliceMs);
      setRecording(true);
    } catch (err) {
      const msg =
        (err as any)?.message ||
        "Microphone permission denied or MediaRecorder not supported.";
      setErrMsg(msg);
      onError?.(err);
      setRecording(false);
      stopVisual();
    }
  }

  function stop() {
    try {
      if (mediaRecRef.current && mediaRecRef.current.state !== "inactive") {
        mediaRecRef.current.stop();
      }
    } catch (e) {
      onError?.(e);
    } finally {
      setRecording(false);
    }
  }

  if (!supported) {
    return (
      <div className="text-sm text-slate-500">
        Microphone/MediaRecorder not supported or insecure context.
      </div>
    );
  }

  return (
    <div className={`space-y-2 ${className ?? ""}`}>
      <canvas ref={canvasRef} height={height} className="w-full rounded-xl border bg-white" />
      {errMsg && <div className="text-xs text-red-600">{errMsg}</div>}
      <div className="flex gap-3">
        {!recording ? (
          <button
            className="rounded-lg border px-4 py-2 bg-slate-900 text-white hover:bg-slate-800"
            onClick={start}
            disabled={recording}
          >
            🎙️ Start
          </button>
        ) : (
          <button
            className="rounded-lg border px-4 py-2 bg-red-600 text-white hover:bg-red-500"
            onClick={stop}
          >
            ⏹ Stop
          </button>
        )}
      </div>
    </div>
  );
}
