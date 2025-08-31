import * as React from "react"
import { cn } from "../../lib/utils"


export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "default" | "secondary" | "outline" | "destructive"
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(({ className, variant="default", ...props }, ref) => {
  const base = "inline-flex items-center justify-center rounded-lg text-sm h-10 px-4 py-2 transition-colors disabled:opacity-50 disabled:pointer-events-none shadow"
  const styles = {
    default: "bg-foreground text-background hover:opacity-90",
    secondary: "bg-secondary text-foreground hover:opacity-90",
    outline: "border border-input bg-white hover:bg-secondary",
    destructive: "bg-destructive text-white hover:opacity-90",
  }[variant]
  return <button ref={ref} className={cn(base, styles, className)} {...props} />
})
Button.displayName = "Button"
