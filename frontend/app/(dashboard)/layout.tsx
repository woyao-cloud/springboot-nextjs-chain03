'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Toaster } from 'sonner';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/stores/useAuthStore';
import { Users, LogOut, LayoutDashboard, UserCircle } from 'lucide-react';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const router = useRouter();
  const { user, logout, isAuthenticated } = useAuthStore();

  const handleLogout = async () => {
    await logout();
    router.push('/login');
  };

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b bg-card">
        <div className="container flex h-16 items-center px-4">
          <Link href="/" className="flex items-center gap-2">
            <LayoutDashboard className="h-5 w-5" />
            <h1 className="text-lg font-semibold">User Management System</h1>
          </Link>
          <nav className="ml-auto flex items-center gap-4">
            <Link
              href="/users"
              className="flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
            >
              <Users className="h-4 w-4" />
              Users
            </Link>
            {isAuthenticated && user ? (
              <div className="flex items-center gap-4 ml-4 border-l pl-4">
                <div className="flex items-center gap-2">
                  <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center">
                    <UserCircle className="h-5 w-5 text-primary" />
                  </div>
                  <span className="text-sm font-medium">{user.username}</span>
                </div>
                <Button variant="ghost" size="sm" onClick={handleLogout}>
                  <LogOut className="h-4 w-4 mr-2" />
                  Logout
                </Button>
              </div>
            ) : (
              <Link href="/login">
                <Button variant="ghost" size="sm">
                  Login
                </Button>
              </Link>
            )}
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
