import { create } from 'zustand'

type AuthState = {
  token: string | null
  role: string | null
  setAuth: (t: string, role?: string) => void
  logout: () => void
}

export const useAuth = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  role: localStorage.getItem('role'),
  setAuth: (t, role) => {
    localStorage.setItem('token', t)
    if (role) localStorage.setItem('role', role)
    set({ token: t, role: role ?? null })
  },
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    set({ token: null, role: null })
  }
}))
