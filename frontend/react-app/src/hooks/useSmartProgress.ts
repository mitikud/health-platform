import { useState } from "react"

/** Determinate progress that caps at 90% during upload, then completes on done() */
export function useSmartProgress() {
  const [pct, setPct] = useState(0)
  const [visible, setVisible] = useState(false)

  const start = () => { setPct(0); setVisible(true) }
  const update = (loaded?: number, total?: number) => {
    if (!total || !loaded) return
    const p = Math.min(90, Math.round((loaded / total) * 100))
    setPct(p)
  }
  const done = () => {
    setPct(100)
    setTimeout(() => { setVisible(false); setPct(0) }, 700)
  }

  return { pct, visible, start, update, done }
}
