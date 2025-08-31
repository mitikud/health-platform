import { useState } from 'react'
import api from '../lib/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

export default function RegisterPage() {
  const nav = useNavigate()
  const { t } = useTranslation()
  const [email,setEmail] = useState('')
  const [password,setPassword] = useState('')
  const [role,setRole] = useState<'PATIENT'|'DOCTOR'|'ADMIN'>('PATIENT')

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    await api.post('/auth/register', { email, password, role })
    nav('/login')
  }

  return (
    <div className="max-w-md mx-auto space-y-6">
      <h1 className="text-2xl font-semibold">{t('register')}</h1>
      <form onSubmit={submit} className="space-y-4">
        <div><Label>Email</Label><Input value={email} onChange={e=>setEmail(e.target.value)} /></div>
        <div><Label>Password</Label><Input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></div>
        <div>
          <Label>Role</Label>
          <select className="border rounded-md h-10 px-3 w-full" value={role} onChange={e=>setRole(e.target.value as any)}>
            <option value="PATIENT">Patient</option>
            <option value="DOCTOR">Doctor</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>
        <Button className="w-full" type="submit">{t('register')}</Button>
      </form>
    </div>
  )
}
