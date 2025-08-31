import { cn } from "../../lib/utils"
export const Card = ({children,className}:{children:any; className?:string}) => <div className={cn("rounded-2xl border bg-white", className)}>{children}</div>
export const CardHeader = ({children,className}:{children:any; className?:string}) => <div className={cn("p-5 border-b", className)}>{children}</div>
export const CardTitle = ({children,className}:{children:any; className?:string}) => <h3 className={cn("text-lg font-semibold", className)}>{children}</h3>
export const CardContent = ({children,className}:{children:any; className?:string}) => <div className={cn("p-5", className)}>{children}</div>
