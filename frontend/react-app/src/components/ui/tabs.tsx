import * as React from "react"
export function Tabs({children, defaultValue}:{children:React.ReactNode; defaultValue?:string}){return <div>{children}</div>}
export const TabsList = ({children, className}:{children:React.ReactNode; className?:string}) => <div className={className}>{children}</div>
export const TabsTrigger = ({children, value}:{children:React.ReactNode; value:string}) => <button className="px-3 py-2 border-b-2">{children}</button>
export const TabsContent = ({children, value, className}:{children:React.ReactNode; value:string; className?:string}) => <div className={className}>{children}</div>
