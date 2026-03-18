import { Metadata } from 'next'
import { UserTable } from '@/components/data-display/UserTable'
import { CreateUserDialog } from '@/components/forms/CreateUserDialog'

export const metadata: Metadata = {
  title: 'Users | User Management',
  description: 'Manage system users',
}

export default function UsersPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Users</h2>
          <p className="text-muted-foreground">
            Manage system users and their permissions
          </p>
        </div>
        <CreateUserDialog />
      </div>

      <UserTable />
    </div>
  )
}
