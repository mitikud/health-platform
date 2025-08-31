/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ["class"],
  content: [
    "./index.html",
    "./src/**/*.{ts,tsx}"
  ],
  theme: {
    container: { center: true, padding: "2rem", screens: { "2xl": "1400px" } },
    extend: {
      colors: {
        border: "hsl(214.3 31.8% 91.4%)",
        input: "hsl(214.3 31.8% 91.4%)",
        ring: "hsl(222.2 47.4% 11.2%)",
        background: "hsl(210 20% 98%)",
        foreground: "hsl(222.2 47.4% 11.2%)",
        primary: { DEFAULT: "hsl(222.2 47.4% 11.2%)", foreground: "hsl(210 40% 98%)" },
        secondary: { DEFAULT: "hsl(210 40% 96.1%)", foreground: "hsl(222.2 47.4% 11.2%)" },
        muted: { DEFAULT: "hsl(210 40% 96.1%)", foreground: "hsl(215.4 16.3% 46.9%)" },
        accent: { DEFAULT: "hsl(210 40% 96.1%)", foreground: "hsl(222.2 47.4% 11.2%)" },
        destructive: { DEFAULT: "hsl(0 84.2% 60.2%)", foreground: "hsl(210 40% 98%)" },
      },
      borderRadius: { lg: "0.75rem", xl: "1rem", "2xl": "1.5rem" },
      boxShadow: { soft: "0 10px 30px rgba(0,0,0,.07)" }
    },
  },
  plugins: [require("tailwindcss-animate")],
}
