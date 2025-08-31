import { useState } from 'react'
import { useAuth } from '../store/auth'
import api from '../lib/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export default function LoginPage() {
  const { setAuth } = useAuth()
  const nav = useNavigate()
  const { t } = useTranslation()
  const [email,setEmail] = useState('')
  const [password,setPassword]=useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    try {
      const res = await api.post('/auth/login', { email, password })
      setAuth(res.data.accessToken, (res as any).data.role)
      nav('/')
    } finally { setLoading(false) }
  }

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-semibold">{t('login')}</h1>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <Label htmlFor="email">Email</Label>
          <Input id="email" value={email} onChange={e=>setEmail(e.target.value)} />
        </div>
        <div>
          <Label htmlFor="pw">Password</Label>
          <Input id="pw" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        </div>
        <Button disabled={loading} type="submit" className="w-full">{t('login')}</Button>
      </form>
    </div>
  )
}
