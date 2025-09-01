import * as React from "react";

type Ctx = {
  value?: string;
  setValue: (v: string) => void;
  open: boolean;
  setOpen: (b: boolean) => void;
  options: Record<string, string>;
  registerOption: (v: string, label: string) => void;
};

const SelectCtx = React.createContext<Ctx | null>(null);

export function Select({
  children,
  value,
  defaultValue,
  onValueChange,
}: {
  children: React.ReactNode;
  value?: string;
  defaultValue?: string;
  onValueChange?: (v: string) => void;
}) {
  const controlled = value !== undefined;
  const [internal, setInternal] = React.useState<string | undefined>(defaultValue);
  const [open, setOpen] = React.useState(false);
  const [options, setOptions] = React.useState<Record<string, string>>({});

  const current = controlled ? value : internal;

  const setValue = React.useCallback(
    (v: string) => {
      if (!controlled) setInternal(v);
      onValueChange?.(v);
    },
    [controlled, onValueChange]
  );

  const registerOption = React.useCallback((v: string, label: string) => {
    setOptions((prev) => (prev[v] ? prev : { ...prev, [v]: label }));
  }, []);

  const ctx: Ctx = { value: current, setValue, open, setOpen, options, registerOption };

  return (
    <SelectCtx.Provider value={ctx}>
      <div className="relative inline-block w-full">{children}</div>
    </SelectCtx.Provider>
  );
}

export function SelectTrigger({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) {
  const ctx = React.useContext(SelectCtx)!;
  return (
    <button
      type="button"
      className={`flex items-center justify-between border rounded-md px-3 py-2 ${className ?? ""}`}
      onClick={() => ctx.setOpen(!ctx.open)}
    >
      {children}
    </button>
  );
}

export function SelectValue({ placeholder }: { placeholder?: string }) {
  const ctx = React.useContext(SelectCtx)!;
  const label = ctx.value ? ctx.options[ctx.value] ?? ctx.value : placeholder;
  return <span className="truncate">{label}</span>;
}

export function SelectContent({ children }: { children: React.ReactNode }) {
  const ctx = React.useContext(SelectCtx)!;
  if (!ctx.open) return null;
  return (
    <div className="absolute z-50 mt-1 w-full bg-white border rounded-md p-2 shadow">
      {children}
    </div>
  );
}

export function SelectItem({
  children,
  value,
}: {
  children: React.ReactNode;
  value: string;
}) {
  const ctx = React.useContext(SelectCtx)!;

  const label = React.useMemo(() => {
    if (typeof children === "string") return children;
    const parts: string[] = [];
    React.Children.forEach(children, (c) => {
      if (typeof c === "string") parts.push(c);
    });
    return parts.join(" ").trim() || value;
  }, [children, value]);

  React.useEffect(() => {
    ctx.registerOption(value, label);
  }, [ctx, value, label]);

  return (
    <button
      type="button"
      className="block w-full text-left px-3 py-1 rounded hover:bg-gray-100"
      onClick={() => {
        ctx.setValue(value);
        ctx.setOpen(false);
      }}
    >
      {children}
    </button>
  );
}



// import * as React from "react"
// export const Select = ({ children, defaultValue, onValueChange }:{children:React.ReactNode; defaultValue?:string; onValueChange?:(v:string)=>void})=>{
//   return <div>{children}</div>
// }
// export const SelectTrigger = ({children,className}:{children:React.ReactNode; className?:string}) => <div className={className}>{children}</div>
// export const SelectValue = ({placeholder}:{placeholder?:string}) => <span>{placeholder}</span>
// export const SelectContent = ({children}:{children:React.ReactNode}) => <div className="absolute z-50 bg-white border rounded-md p-2">{children}</div>
// export const SelectItem = ({children, value}:{children:React.ReactNode; value:string}) => <button className="block w-full text-left px-3 py-1 hover:bg-secondary" onClick={()=>{(window as any).__sel?.(value)}}>{children}</button>
