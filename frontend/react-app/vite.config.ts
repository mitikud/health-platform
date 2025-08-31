import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// In Docker prod image, we use Nginx to proxy /api -> gateway.
// For local dev, we can proxy too:
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8080' }
  },
  resolve: { alias: { '@': '/src' } }
})
