import { useEffect, useRef, useState } from "react"
import { Button } from "../components/ui/button"

type Props = { onStop: (file: File) => void; height?: number }

export default function RecorderWave({ onStop, height = 80 }: Props) {
  const [recording, setRecording] = useState(false)
  const [supported, setSupported] = useState(false)

  const mediaRecRef = useRef<MediaRecorder | null>(null)
  const mediaStreamRef = useRef<MediaStream | null>(null)
  const chunks = useRef<BlobPart[]>([])

  const canvasRef = useRef<HTMLCanvasElement | null>(null)
  const rafRef = useRef<number | null>(null)
  const analyserRef = useRef<AnalyserNode | null>(null)
  const audioCtxRef = useRef<AudioContext | null>(null)
  const sourceRef = useRef<MediaStreamAudioSourceNode | null>(null)

  useEffect(() => {
    setSupported(Boolean(navigator.mediaDevices && window.MediaRecorder && window.AudioContext))
    return () => stopVisual()
  }, [])

  async function start() {
    if (!supported) return
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaStreamRef.current = stream

    // 1) Start recorder
    const rec = new MediaRecorder(stream, { mimeType: "audio/webm" })
    mediaRecRef.current = rec
    chunks.current = []
    rec.ondataavailable = e => { if (e.data.size > 0) chunks.current.push(e.data) }
    rec.onstop = () => {
      stream.getTracks().forEach(t => t.stop())
      const blob = new Blob(chunks.current, { type: "audio/webm" })
      const file = new File([blob], `recording-${Date.now()}.webm`, { type: blob.type })
      onStop(file)
      stopVisual()
    }

    // 2) Visualize: connect stream -> analyser -> draw on canvas
    const ACtx = window.AudioContext || (window as any).webkitAudioContext
    const ctx = new ACtx()
    audioCtxRef.current = ctx
    const source = ctx.createMediaStreamSource(stream)
    sourceRef.current = source
    const analyser = ctx.createAnalyser()
    analyser.fftSize = 2048
    analyserRef.current = analyser
    source.connect(analyser)

    draw()
    rec.start()
    setRecording(true)
  }

  function draw() {
    const analyser = analyserRef.current
    const canvas = canvasRef.current
    if (!analyser || !canvas) return
    const c = canvas.getContext("2d")!
    const n = analyser.fftSize
    const data = new Uint8Array(n)

    const render = () => {
      analyser.getByteTimeDomainData(data)
      const { width, height } = canvas
      c.clearRect(0, 0, width, height)
      c.fillStyle = "#ffffff"
      c.fillRect(0, 0, width, height)
      c.lineWidth = 2
      c.strokeStyle = "#111827" // neutral-900
      c.beginPath()
      const slice = width / n
      let x = 0
      for (let i = 0; i < n; i++) {
        const v = data[i] / 128.0
        const y = (v * height) / 2
        i === 0 ? c.moveTo(x, y) : c.lineTo(x, y)
        x += slice
      }
      c.lineTo(width, height / 2)
      c.stroke()
      rafRef.current = requestAnimationFrame(render)
    }
    rafRef.current = requestAnimationFrame(render)
  }

  function stopVisual() {
    if (rafRef.current) cancelAnimationFrame(rafRef.current)
    rafRef.current = null
    try { sourceRef.current?.disconnect(); analyserRef.current?.disconnect() } catch {}
    try { audioCtxRef.current?.close() } catch {}
    analyserRef.current = null
    sourceRef.current = null
    audioCtxRef.current = null
  }

  function stop() {
    mediaRecRef.current?.stop()
    setRecording(false)
  }

  if (!supported) return <div className="text-sm text-muted-foreground">Microphone/AudioContext not supported.</div>

  return (
    <div className="space-y-2">
      <canvas ref={canvasRef} height={height} className="w-full rounded-xl border bg-white" />
      <div className="flex gap-3">
        {!recording
          ? <Button onClick={start}>🎙️ Start</Button>
          : <Button variant="destructive" onClick={stop}>⏹ Stop</Button>}
      </div>
    </div>
  )
}
