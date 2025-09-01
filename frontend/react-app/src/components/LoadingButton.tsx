import { Button, type ButtonProps } from "../components/ui/button"

export default function LoadingButton(
  { loading, children, ...rest }:
  ButtonProps & { loading?: boolean }
) {
  return (
    <Button disabled={loading || rest.disabled} {...rest}>
      {loading && (
        <span className="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-white/70 border-t-transparent" />
      )}
      {children}
    </Button>
  )
}
