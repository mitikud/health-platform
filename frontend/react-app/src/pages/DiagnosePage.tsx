import { useState, useRef, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import api from '../lib/api'
import { Button } from '../components/ui/button'
import { Textarea } from '../components/ui/textarea'
import { Input } from '../components/ui/input'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs'
import { ResultCard } from '../components/ResultCard'
import { Howl } from 'howler'
import LoadingButton from '../components/LoadingButton'
import { toast } from "../components/ui/sonner";
// import Recorder from '../components/Recorder'
// import RecorderWave  from '../components/RecorderWave'
import RecorderWave from '../components/RecorderWave'

import UploadProgress from '../components/UploadProgress'
import { useSmartProgress } from '../hooks/useSmartProgress'

import SpeechLanguageSelect from '../components/SpeechLanguageSelect'


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

  const audioProg = useSmartProgress()
  const imageProg = useSmartProgress()


  const lang = i18n.language
  // const speechLocale = lang === 'am' ? 'am-ET' : lang === 'ti' ? 'ti-ER' : 'en-US'

  

const [speechLocale, setSpeechLocale] = useState(
  i18n.language === 'am' ? 'am-ET' : i18n.language === 'ti' ? 'ti-ER' : 'en-US'
)

// const mode = (lang === 'am' || lang === 'ti') ? 'CLOUD' : 'AUTO'
  const mode = (speechLocale.startsWith('am') || speechLocale.startsWith('ti')) ? 'CLOUD' : 'AUTO'
  const headers = { 'Content-Type': 'multipart/form-data', 'X-Processing-Mode': mode }

useEffect(() => {
  // keep default in sync when user flips UI language (but don't override manual choice)
  setSpeechLocale(prev => {
    const auto = i18n.language === 'am' ? 'am-ET' : i18n.language === 'ti' ? 'ti-ER' : 'en-US'
    // if user hasn’t touched it (still same family), adjust; else keep
    return (prev.startsWith('en') || prev.startsWith('am') || prev.startsWith('ti')) ? auto : prev
  })
}, [i18n.language])

  // const analyzeText = async () => {
  //   setBusy(true)
  //   try {
  //     const res = await api.post('/diagnosis/analyze', { text, preferredLang: lang })
  //     setResult(res.data)
  //   } finally { setBusy(false) }
  // }

  const analyzeText = async () => {
  if (!text.trim()) { toast.info("Please enter symptoms"); return }
  setBusy(true)
  const t = toast.loading("Analyzing text…")
  try {
    const res = await api.post('/diagnosis/analyze', { text, preferredLang: lang })
    setResult(res.data)
    toast.success("Analysis ready", { id: t })
  } catch (e:any) {
    toast.error(e?.response?.data?.message || "Failed to analyze text", { id: t })
  } finally { setBusy(false) }
}

  const analyzeAudio = async ()=>{
    const file = recordedFile ?? audioRef.current
    if (!file) { toast.info("Record or choose an audio file"); return }
    setBusy(true)
    audioProg.start()
    const t = toast.loading("Uploading audio…")
    try {
      const fd = new FormData()
      fd.append('audio', file)
      fd.append('lang', lang)        // keep existing
      fd.append('locale', speechLocale)  // NEW: tells backend STT which language to use
      // STEP 2 will add locale here
      // const res = await api.post('/diagnosis/analyze-audio', fd, { headers: { 'Content-Type':'multipart/form-data' } })
      // const res= await api.post('/diagnosis/analyze-audio', fd, { headers })
      
      // const res = await api.post('/diagnosis/analyze-audio', fd, {
      //   headers: { 'Content-Type':'multipart/form-data' },
      //   onUploadProgress: (e) => audioProg.update(e.loaded, e.total)
      // })
      const res = await api.post('/diagnosis/analyze-audio', fd, {
  headers: { 'Content-Type':'multipart/form-data', 'X-Processing-Mode': mode },
  onUploadProgress: (e)=> audioProg.update(e.loaded, e.total)
})


      audioProg.done()

      setResult(res.data)
      toast.success("Audio analyzed", { id: t })
    } catch (e:any) {
      toast.error(e?.response?.data?.message || "Audio analysis failed", { id: t })

      audioProg.done()

    } finally { setBusy(false) }
  }
  // const analyzeAudio = async () => {
  //   if (!audioRef.current) return
  //   setBusy(true)
  //   try {
  //     const fd = new FormData()
  //     fd.append('audio', audioRef.current)
  //     fd.append('lang', lang)
  //     const res = await api.post('/diagnosis/analyze-audio', fd, { headers: { 'Content-Type':'multipart/form-data' } })
  //     setResult(res.data)
  //   } finally { setBusy(false) }
  // }

  // const analyzeImage = async () => {
  //   if (!imageRef.current) return
  //   setBusy(true)
  //   try {
  //     const fd = new FormData()
  //     fd.append('image', imageRef.current)
  //     fd.append('lang', lang)
  //     const res = await api.post('/diagnosis/analyze-image', fd, { headers: { 'Content-Type':'multipart/form-data' } })
  //     setResult(res.data)
  //   } finally { setBusy(false) }
  // }
const analyzeImage = async () => {
  if (!imageRef.current) { toast.info("Choose a prescription photo"); return }
  setBusy(true)
  imageProg.start()
  const t = toast.loading("Uploading image…")
  try {
    const fd = new FormData()
    fd.append('image', imageRef.current)
    fd.append('lang', lang)
    fd.append('locale', speechLocale)  // NEW: tells backend STT which language to use
    // const res = await api.post('/diagnosis/analyze-image', fd, { headers: { 'Content-Type':'multipart/form-data' } })
    
    const res = await api.post('/diagnosis/analyze-image', fd, {
      headers: { 'Content-Type':'multipart/form-data' },
      onUploadProgress: (e) => imageProg.update(e.loaded, e.total)
    })
    imageProg.done()
    
    setResult(res.data)
    toast.success("Image analyzed", { id: t })
  } catch (e:any) {
    toast.error(e?.response?.data?.message || "Image analysis failed", { id: t })
    imageProg.done()
  } finally { setBusy(false) }
}

  const speak = async () => {
    if (!result) return
    const text = [...(result.analysis?.possible || []), ...(result.analysis?.recommendations || [])].join('. ')
    // const locale = lang === 'am' ? 'am-ET' : lang === 'ti' ? 'ti-ER' : 'en-US'
    
    const locale = speechLocale // was derived; now use the explicit selection

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
<SpeechLanguageSelect value={speechLocale} onChange={setSpeechLocale} />


<TabsContent value="audio" className="space-y-3">
  <RecorderWave onStop={(f)=> setRecordedFile(f)} />
    
  <div className="text-xs text-muted-foreground">
    {recordedFile ? `Ready: ${recordedFile.name} (${Math.round(recordedFile.size/1024)} KB)` : 'Record, then Submit'}
  </div>
  <div className="text-xs text-muted-foreground">
  Speech locale: <span className="font-mono">{speechLocale}</span>
</div>

<UploadProgress pct={audioProg.pct} showing={audioProg.visible} label="Uploading audio…" />

  <Input type="file" accept="audio/*" onChange={e=> (audioRef.current = e.target.files?.[0] ?? null)} />
  <div className="flex justify-end">
    {/* <Button
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
    </Button> */}
    <LoadingButton
  onClick={analyzeAudio}
  loading={busy}
  // disabled={(!recordedFile && !audioRef.current)}
  disabled={busy || (!recordedFile && !audioRef.current)}
>
  {t('submit')}
</LoadingButton>

  </div>
</TabsContent>

          <TabsContent value="image" className="space-y-3">
            <Input type="file" accept="image/*" onChange={e=> imageRef.current = e.target.files?.[0] ?? null} />
            <div className="flex justify-end">
              {/* <Button onClick={analyzeImage} disabled={busy || !imageRef.current}>{t('submit')}</Button> */}
              <LoadingButton
  onClick={analyzeImage}
  loading={busy}
  disabled={busy || !imageRef.current}
>
  {t('submit')}
</LoadingButton>

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
