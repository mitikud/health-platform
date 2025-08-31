import * as React from "react"
export const Select = ({ children, defaultValue, onValueChange }:{children:React.ReactNode; defaultValue?:string; onValueChange?:(v:string)=>void})=>{
  return <div>{children}</div>
}
export const SelectTrigger = ({children,className}:{children:React.ReactNode; className?:string}) => <div className={className}>{children}</div>
export const SelectValue = ({placeholder}:{placeholder?:string}) => <span>{placeholder}</span>
export const SelectContent = ({children}:{children:React.ReactNode}) => <div className="absolute z-50 bg-white border rounded-md p-2">{children}</div>
export const SelectItem = ({children, value}:{children:React.ReactNode; value:string}) => <button className="block w-full text-left px-3 py-1 hover:bg-secondary" onClick={()=>{(window as any).__sel?.(value)}}>{children}</button>
