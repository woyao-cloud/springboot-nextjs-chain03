import { Metadata } from 'next'
import { Toaster } from 'sonner'

export const metadata: Metadata = {
  title: 'User Management Dashboard',
  description: 'User management system dashboard',
}

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen bg-background">
      <header className="border-b">
        <div className="container flex h-16 items-center px-4">
          <h1 className="text-lg font-semibold">User Management System</h1>
          <nav className="ml-auto flex gap-4">
            <a href="/users" className="text-sm font-medium text-muted-foreground hover:text-foreground">
              Users
            </a>
          </nav>
        </div>
      </header>
      <main className="container py-6 px-4">
        {children}
      </main>
      <Toaster position="top-right" />
    </div>
  )
}
