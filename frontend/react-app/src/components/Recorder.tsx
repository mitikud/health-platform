import { useEffect, useRef, useState } from "react"
import { Button } from "../components/ui/button"

type Props = { onStop: (file: File) => void }

export default function Recorder({ onStop }: Props) {
  const [recording, setRecording] = useState(false)
  const [supported, setSupported] = useState(false)
  const mediaRef = useRef<MediaRecorder | null>(null)
  const chunks = useRef<BlobPart[]>([])

  useEffect(() => {
    setSupported(Boolean(navigator.mediaDevices && window.MediaRecorder))
  }, [])

  async function start() {
    if (!supported) return
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    const rec = new MediaRecorder(stream, { mimeType: "audio/webm" })
    mediaRef.current = rec
    chunks.current = []

    rec.ondataavailable = (e) => { if (e.data.size > 0) chunks.current.push(e.data) }
    rec.onstop = () => {
      stream.getTracks().forEach(t => t.stop())
      const blob = new Blob(chunks.current, { type: "audio/webm" })
      const file = new File([blob], `recording-${Date.now()}.webm`, { type: blob.type })
      onStop(file)
    }

    rec.start()
    setRecording(true)
  }

  function stop() {
    mediaRef.current?.stop()
    setRecording(false)
  }

  if (!supported) {
    return <div className="text-sm text-muted-foreground">Microphone not supported in this browser/context.</div>
  }

  return (
    <div className="flex gap-3">
      {!recording
        ? <Button onClick={start}>🎙️ Start</Button>
        : <Button variant="destructive" onClick={stop}>⏹ Stop</Button>}
    </div>
  )
}
