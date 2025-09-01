import { Route, Routes, Navigate, Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Languages, Stethoscope, LogOut, LogIn, Pill } from 'lucide-react'
import { useAuth } from './store/auth'
import { LanguageSwitcher } from './components/LanguageSwitcher'
import { Button } from './components/ui/button'
import DiagnosePage from './pages/DiagnosePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import MedicationPage from './pages/MedicationPage'
import { Toaster } from "./components/ui/sonner";


export default function App() {
  const { t } = useTranslation()
  const { token, logout } = useAuth()

  return (
    <div className="min-h-screen">
      <header className="border-b bg-white sticky top-0 z-50">
        <div className="container flex items-center justify-between py-3">
          <Link to="/" className="flex items-center gap-2 font-semibold">
            <Stethoscope className="size-5" />
            <span>{t('app_name')}</span>
          </Link>
          <div className="flex items-center gap-3">
            <LanguageSwitcher />
            {!token ? (
              <div className="flex gap-2">
                <Link to="/login"><Button variant="outline"><LogIn className="mr-2 size-4" />{t('login')}</Button></Link>
                <Link to="/register"><Button>{t('register')}</Button></Link>
              </div>
            ) : (
              <Button variant="secondary" onClick={logout}><LogOut className="mr-2 size-4" />{t('logout')}</Button>
            )}
          </div>
        </div>
      </header>

      <main className="container py-6">
        <Routes>
          <Route path="/" element={<DiagnosePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/medications" element={token ? <MedicationPage /> : <Navigate to="/login" />} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>

      <footer className="border-t py-6 text-center text-sm text-muted-foreground">
        <div className="container">&copy; {new Date().getFullYear()} · {t('app_name')}</div>
      </footer>
      <Toaster richColors closeButton />
    </div>
  )
}
