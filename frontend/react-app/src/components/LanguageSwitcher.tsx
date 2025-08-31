import { useTranslation } from 'react-i18next'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select'
import { Languages } from 'lucide-react'

export const LanguageSwitcher = () => {
  const { i18n } = useTranslation()
  return (
    <div className="flex items-center gap-2">
      <Languages className="size-4 opacity-70" />
      <Select defaultValue={i18n.language} onValueChange={(v: string | undefined) => i18n.changeLanguage(v)}>
        <SelectTrigger className="w-[130px]"><SelectValue placeholder="Language" /></SelectTrigger>
        <SelectContent>
          <SelectItem value="en">English</SelectItem>
          <SelectItem value="am">Amharic</SelectItem>
          <SelectItem value="ti">Tigrinya</SelectItem>
        </SelectContent>
      </Select>
    </div>
  )
}
