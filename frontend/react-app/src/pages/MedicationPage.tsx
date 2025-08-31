import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import api from '../lib/api'
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card'
import { Badge } from '../components/ui/badge'
import { Pill } from 'lucide-react'

type Interaction = { with: string; severity: string; mechanism: string; action: string }
type Item = {
  drug: string; dose: string; rationale: string;
  benefits: string[]; sideEffectsCommon: string[]; sideEffectsSerious: string[];
  contraindications: string[]; saferAlternatives: string[]; interactions: Interaction[];
  riskScore: number
}
type Plan = {
  id: string
  language: string
  freeTextDiagnosis?: string
  diagnosisCodes?: string[]
  items: Item[]
  status: string
  createdAt: string
}

export default function MedicationPage() {
  const [sp] = useSearchParams()
  const planId = sp.get('planId')
  const [plan, setPlan] = useState<Plan | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const run = async () => {
      if (!planId) return
      setLoading(true)
      try {
        const res = await api.get(`/medications/plan/${planId}`)
        setPlan(res.data)
      } finally { setLoading(false) }
    }
    run()
  }, [planId])

  if (!planId) return <div className="text-muted-foreground">No planId in URL.</div>
  if (loading) return <div className="text-muted-foreground">Loading plan…</div>
  if (!plan) return <div className="text-muted-foreground">Plan not found.</div>

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold flex items-center gap-2">
        <Pill className="size-5" /> Medication Plan
      </h1>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-3">
            Plan {plan.id}
            <Badge>{plan.status}</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {plan.freeTextDiagnosis && <div><b>Diagnosis:</b> {plan.freeTextDiagnosis}</div>}
          {!!plan.diagnosisCodes?.length && <div><b>Codes:</b> {plan.diagnosisCodes.join(', ')}</div>}
          <div className="text-sm text-muted-foreground">Created: {new Date(plan.createdAt).toLocaleString()}</div>
        </CardContent>
      </Card>

      <div className="grid gap-4">
        {plan.items.map((it, idx) => (
          <Card key={idx} className="shadow-soft">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <span>{it.drug}</span>
                <Badge>risk {(it.riskScore*100|0)}%</Badge>
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div><b>Dose:</b> {it.dose}</div>
              <div><b>Rationale:</b> {it.rationale}</div>
              {!!it.benefits?.length && <div><b>Benefits:</b> {it.benefits.join('; ')}</div>}
              {!!it.sideEffectsCommon?.length && <div><b>Common side effects:</b> {it.sideEffectsCommon.join('; ')}</div>}
              {!!it.sideEffectsSerious?.length && <div><b>Serious side effects:</b> {it.sideEffectsSerious.join('; ')}</div>}
              {!!it.contraindications?.length && <div><b>Contraindications:</b> {it.contraindications.join('; ')}</div>}
              {!!it.saferAlternatives?.length && <div><b>Safer alternatives:</b> {it.saferAlternatives.join('; ')}</div>}
              {!!it.interactions?.length && (
                <div>
                  <b>Interactions:</b>
                  <ul className="list-disc ml-6">
                    {it.interactions.map((ix, i) => (
                      <li key={i}>
                        {ix.with} — {ix.severity} ({ix.mechanism}). {ix.action}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}


// import { useEffect, useState } from 'react'
// import { useSearchParams } from 'react-router-dom'
// import api from '../lib/api'
// import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card'
// import { Badge } from '../components/ui/badge'
// import { Pill } from 'lucide-react'

// type Interaction = { with: string; severity: string; mechanism: string; action: string }
// type Item = {
//   drug: string; dose: string; rationale: string;
//   benefits: string[]; sideEffectsCommon: string[]; sideEffectsSerious: string[];
//   contraindications: string[]; saferAlternatives: string[]; interactions: Interaction[];
//   riskScore: number
// }
// type Response = { planId: string; language: string; recommendations: Item[]; disclaimer: string }

// export default function MedicationPage() {
//   const [sp] = useSearchParams()
//   const planId = sp.get('planId')
//   const [data, setData] = useState<Response|null>(null)

//   useEffect(()=> {
//     // This page shows the last response handed by /recommend;
//     // If you want to fetch by planId, you’d add a GET endpoint in the backend.
//     // For demo we keep `data` null until someone navigates here with ?planId=
//   }, [planId])

//   if (!planId) return <div className="text-muted-foreground">No plan provided yet.</div>

//   return (
//     <div className="space-y-6">
//       <h1 className="text-2xl font-semibold flex items-center gap-2"><Pill className="size-5" /> Medication Plan</h1>
//       <div className="grid gap-4">
//         {/* You might render from localStorage or add a GET /plan/{id} later */}
//         <p className="text-muted-foreground">Plan ID: {planId}</p>
//         {/* placeholder message */}
//         <p className="text-sm">Open the Network tab — you were redirected here with the new planId. Add a GET endpoint to load full plan details.</p>
//       </div>
//     </div>
//   )
// }
