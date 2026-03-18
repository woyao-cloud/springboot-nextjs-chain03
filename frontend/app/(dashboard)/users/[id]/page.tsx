import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import { UserDetail } from '@/components/data-display/UserDetail'
import { userApi } from '@/lib/api/userApi'

interface UserDetailPageProps {
  params: {
    id: string
  }
}

export async function generateMetadata({ params }: UserDetailPageProps): Promise<Metadata> {
  try {
    const response = await userApi.getUserById(params.id)
    const user = response.data
    return {
      title: `${user.username} | User Details`,
      description: `View details for user ${user.username}`,
    }
  } catch {
    return {
      title: 'User Details | User Management',
      description: 'View user details',
    }
  }
}

export default async function UserDetailPage({ params }: UserDetailPageProps) {
  try {
    const response = await userApi.getUserById(params.id)
    const user = response.data

    return (
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">User Details</h2>
          <p className="text-muted-foreground">
            View and manage user information
          </p>
        </div>
        <UserDetail user={user} />
      </div>
    )
  } catch {
    notFound()
  }
}
