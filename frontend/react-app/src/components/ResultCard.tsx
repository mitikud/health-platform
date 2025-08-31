import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card'
import { useTranslation } from 'react-i18next'

export function ResultCard(props: {
  possible: string[]; recommendations: string[]; confidence?: number; onSpeak?: () => void; ttsLabel?: string
}) {
  const { t } = useTranslation()
  return (
    <Card className="shadow-soft">
      <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <CardTitle className="text-xl">{t('diagnosis')}</CardTitle>
        <div className="text-sm text-muted-foreground">
          {t('confidence')}: {(props.confidence ?? 0)*100|0}%
        </div>
      </CardHeader>
      <CardContent className="space-y-5">
        <section>
          <h4 className="font-semibold mb-2">{t('diagnosis')}</h4>
          <ul className="list-disc pl-5 space-y-1">
            {props.possible?.map((p,i)=> <li key={i}>{p}</li>)}
          </ul>
        </section>
        <section>
          <h4 className="font-semibold mb-2">{t('recommendations')}</h4>
          <ul className="list-disc pl-5 space-y-1">
            {props.recommendations?.map((p,i)=> <li key={i}>{p}</li>)}
          </ul>
        </section>
        {props.onSpeak && (
          <button onClick={props.onSpeak} className="text-sm underline">{props.ttsLabel ?? 'Listen'}</button>
        )}
      </CardContent>
    </Card>
  )
}
