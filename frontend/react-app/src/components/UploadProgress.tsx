import { Progress } from "./ui/progress"

export default function UploadProgress({ pct, showing, label }:{
  pct: number; showing: boolean; label?: string
}) {
  if (!showing) return null
  return (
    <div className="space-y-1">
      <div className="text-xs text-muted-foreground">{label ?? "Uploading…"}</div>
      <Progress value={pct} />
    </div>
  )
}
