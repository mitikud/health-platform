import { useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import api from '../lib/api'
import { Button } from '../components/ui/button'
import { Textarea } from '../components/ui/textarea'
import { Input } from '../components/ui/input'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs'
import { ResultCard } from '../components/ResultCard'
import { Howl } from 'howler'
// import Recorder from '../components/Recorder'
import RecorderWave  from '../components/RecorderWave'

type DiagnosisPayload = { possible: string[]; recommendations: string[] }
type DiagnosisResponse = {
  analysis: DiagnosisPayload
  confidence: number
  language: string
  source: string
  requestId: string
}

export default function DiagnosePage() {
  const { t, i18n } = useTranslation()
  const [text, setText] = useState('')
  const [result, setResult] = useState<DiagnosisResponse | null>(null)
  const [busy, setBusy] = useState(false)
  const audioRef = useRef<File | null>(null)
  const imageRef = useRef<File | null>(null)

  const [recordedFile, setRecordedFile] = useState<File | null>(null)

  const lang = i18n.language

  const analyzeText = async () => {
    setBusy(true)
    try {
      const res = await api.post('/diagnosis/analyze', { text, preferredLang: lang })
      setResult(res.data)
    } finally { setBusy(false) }
  }

  const analyzeAudio = async () => {
    if (!audioRef.current) return
    setBusy(true)
    try {
      const fd = new FormData()
      fd.append('audio', audioRef.current)
      fd.append('lang', lang)
      const res = await api.post('/diagnosis/analyze-audio', fd, { headers: { 'Content-Type':'multipart/form-data' } })
      setResult(res.data)
    } finally { setBusy(false) }
  }

  const analyzeImage = async () => {
    if (!imageRef.current) return
    setBusy(true)
    try {
      const fd = new FormData()
      fd.append('image', imageRef.current)
      fd.append('lang', lang)
      const res = await api.post('/diagnosis/analyze-image', fd, { headers: { 'Content-Type':'multipart/form-data' } })
      setResult(res.data)
    } finally { setBusy(false) }
  }

  const speak = async () => {
    if (!result) return
    const text = [...(result.analysis?.possible || []), ...(result.analysis?.recommendations || [])].join('. ')
    const locale = lang === 'am' ? 'am-ET' : lang === 'ti' ? 'ti-ER' : 'en-US'
    const url = `/api/tts/speak?text=${encodeURIComponent(text)}&lang=${encodeURIComponent(locale)}`
    const sound = new Howl({ src: [url], html5: true })
    sound.play()
  }

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <div className="space-y-6">
        <Tabs defaultValue="text">
          <TabsList className="grid grid-cols-3">
            <TabsTrigger value="text">{t('analyze_text')}</TabsTrigger>
            <TabsTrigger value="audio">{t('analyze_audio')}</TabsTrigger>
            <TabsTrigger value="image">{t('analyze_image')}</TabsTrigger>
          </TabsList>

          <TabsContent value="text" className="space-y-3">
            <Textarea value={text} onChange={e=>setText(e.target.value)} placeholder={t('symptoms_placeholder')!} rows={7} />
            <div className="flex justify-end">
              <Button onClick={analyzeText} disabled={busy}>{t('submit')}</Button>
            </div>
          </TabsContent>

         

          {/* <TabsContent value="audio" className="space-y-3">
  <Recorder onStop={(f)=> setRecordedFile(f)} />
  <div className="text-xs text-muted-foreground">
    {recordedFile ? `Ready: ${recordedFile.name} (${Math.round(recordedFile.size/1024)} KB)` : 'Record, then Submit'}
  </div>
  <Input type="file" accept="audio/*" onChange={e=> (audioRef.current = e.target.files?.[0] ?? null)} />
  <div className="flex justify-end">
    <Button
      onClick={async ()=>{
        const file = recordedFile ?? audioRef.current
        if (!file) return
        setBusy(true)
        try {
          const fd = new FormData()
          fd.append('audio', file)
          fd.append('lang', lang)
          const res = await api.post('/diagnosis/analyze-audio', fd, { headers: { 'Content-Type':'multipart/form-data' } })
          setResult(res.data)
        } finally { setBusy(false) }
      }}
      disabled={busy || (!recordedFile && !audioRef.current)}
    >
      {t('submit')}
    </Button>
  </div>
</TabsContent> */}


<TabsContent value="audio" className="space-y-3">
  <RecorderWave onStop={(f)=> setRecordedFile(f)} />
  <div className="text-xs text-muted-foreground">
    {recordedFile ? `Ready: ${recordedFile.name} (${Math.round(recordedFile.size/1024)} KB)` : 'Record, then Submit'}
  </div>
  <Input type="file" accept="audio/*" onChange={e=> (audioRef.current = e.target.files?.[0] ?? null)} />
  <div className="flex justify-end">
    <Button
      onClick={async ()=>{
        const file = recordedFile ?? audioRef.current
        if (!file) return
        setBusy(true)
        try {
          const fd = new FormData()
          fd.append('audio', file)
          fd.append('lang', lang)
          const res = await api.post('/diagnosis/analyze-audio', fd, { headers: { 'Content-Type':'multipart/form-data' } })
          setResult(res.data)
        } finally { setBusy(false) }
      }}
      disabled={busy || (!recordedFile && !audioRef.current)}
    >
      {t('submit')}
    </Button>
  </div>
</TabsContent>

          <TabsContent value="image" className="space-y-3">
            <Input type="file" accept="image/*" onChange={e=> imageRef.current = e.target.files?.[0] ?? null} />
            <div className="flex justify-end">
              <Button onClick={analyzeImage} disabled={busy || !imageRef.current}>{t('submit')}</Button>
            </div>
          </TabsContent>
        </Tabs>
      </div>

      <div className="space-y-4">
        {result && (
          <ResultCard
            possible={result.analysis?.possible || []}
            recommendations={result.analysis?.recommendations || []}
            confidence={result.confidence}
            onSpeak={speak}
            ttsLabel={t('tts') as string}
          />
        )}
        <div className="flex gap-2">
          <Button variant="secondary" onClick={async ()=>{
            if (!result) return
            const payload = {
              diagnosisCodes: [],
              freeTextDiagnosis: result.analysis?.possible?.[0] || '',
              language: lang
            }
            const res = await api.post('/medications/recommend', payload)
            window.location.href = '/medications?planId=' + res.data.planId
          }}>
            {t('generate_medication')}
          </Button>
        </div>
      </div>
    </div>
  )
}
