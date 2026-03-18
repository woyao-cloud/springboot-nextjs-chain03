import { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Authentication | User Management',
  description: 'Login or register to access the system',
}

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted p-4">
      <div className="w-full max-w-md">
        {children}
      </div>
    </div>
  )
}
