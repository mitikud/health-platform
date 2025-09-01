"use client";
import { Toaster as SonnerToaster } from "sonner";
export { toast } from "sonner";
export function Toaster(props: React.ComponentProps<typeof SonnerToaster>) {
  return <SonnerToaster {...props} />;
}
