import { Select,
     SelectContent, 
     SelectItem,
      SelectTrigger, 
      SelectValue } from "../components/ui/select"

const OPTIONS = [
  { code: "en-US", label: "English (US)" },
  { code: "am-ET", label: "Amharic (Ethiopia)" },
  { code: "ti-ER", label: "Tigrinya (Eritrea)" },
]

export default function SpeechLanguageSelect({
  value, 
  onChange
}:{ value: string; 
    onChange: (v:string)=>void }) {
  return (
    <div className="flex items-center gap-2">
      <span className="text-sm text-muted-foreground">Speech locale</span>
      <Select value={value} onValueChange={onChange}>
        <SelectTrigger className="w-[220px]">
          <SelectValue placeholder="Select speech locale" />
        </SelectTrigger>
        <SelectContent>
          {OPTIONS.map(o => <SelectItem key={o.code} value={o.code}>{o.label}</SelectItem>)}
        </SelectContent>
      </Select>
    </div>
  )
}
