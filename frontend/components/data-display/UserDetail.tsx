'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { User, UserUpdateRequest } from '@/types';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useUserStore } from '@/stores/useUserStore';
import {
  ArrowLeft,
  Edit2,
  Save,
  X,
  UserCheck,
  UserX,
  Trash2,
  Mail,
  Calendar,
  Shield,
  AlertCircle,
  Loader2
} from 'lucide-react';
import { toast } from 'sonner';

interface UserDetailProps {
  user: User;
}

export function UserDetail({ user }: UserDetailProps) {
  const router = useRouter();
  const { activateUser, deactivateUser, deleteUser } = useUserStore();
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState<UserUpdateRequest>({
    email: user.email,
    firstName: user.firstName || '',
    lastName: user.lastName || '',
  });

  const handleGoBack = () => {
    router.push('/users');
  };

  const handleToggleActive = async () => {
    setIsLoading(true);
    setError(null);
    try {
      if (user.isActive) {
        await deactivateUser(user.id);
        toast.success(`User "${user.username}" deactivated`);
      } else {
        await activateUser(user.id);
        toast.success(`User "${user.username}" activated`);
      }
    } catch {
      setError('Failed to update user status');
      toast.error('Failed to update user status');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm(`Are you sure you want to delete user "${user.username}"?`)) {
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      await deleteUser(user.id);
      toast.success(`User "${user.username}" deleted successfully`);
      router.push('/users');
    } catch {
      setError('Failed to delete user');
      toast.error('Failed to delete user');
      setIsLoading(false);
    }
  };

  const handleSave = async () => {
    setIsLoading(true);
    setError(null);
    try {
      // In a real app, you would call an update API here
      toast.success('User updated successfully');
      setIsEditing(false);
    } catch {
      setError('Failed to update user');
      toast.error('Failed to update user');
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="space-y-6">
      {/* Header Actions */}
      <div className="flex items-center justify-between">
        <Button variant="outline" onClick={handleGoBack}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Users
        </Button>
        <div className="flex items-center gap-2">
          {isEditing ? (
            <>
              <Button
                variant="outline"
                onClick={() => setIsEditing(false)}
                disabled={isLoading}
              >
                <X className="mr-2 h-4 w-4" />
                Cancel
              </Button>
              <Button onClick={handleSave} disabled={isLoading}>
                {isLoading ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  <Save className="mr-2 h-4 w-4" />
                )}
                Save
              </Button>
            </>
          ) : (
            <Button variant="outline" onClick={() => setIsEditing(true)}>
              <Edit2 className="mr-2 h-4 w-4" />
              Edit
            </Button>
          )}
        </div>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* User Info Card */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="h-16 w-16 rounded-full bg-primary/10 flex items-center justify-center">
                <span className="text-2xl font-bold text-primary">
                  {user.username.charAt(0).toUpperCase()}
                </span>
              </div>
              <div>
                <CardTitle className="text-xl">{user.username}</CardTitle>
                <div className="flex items-center gap-2 mt-1">
                  <Badge variant={user.isActive ? 'default' : 'secondary'}>
                    {user.isActive ? 'Active' : 'Inactive'}
                  </Badge>
                  {user.isVerified && (
                    <Badge variant="outline" className="text-green-600">
                      Verified
                    </Badge>
                  )}
                </div>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={handleToggleActive}
                disabled={isLoading}
              >
                {user.isActive ? (
                  <>
                    <UserX className="mr-2 h-4 w-4" />
                    Deactivate
                  </>
                ) : (
                  <>
                    <UserCheck className="mr-2 h-4 w-4" />
                    Activate
                  </>
                )}
              </Button>
              <Button
                variant="destructive"
                size="sm"
                onClick={handleDelete}
                disabled={isLoading}
              >
                <Trash2 className="mr-2 h-4 w-4" />
                Delete
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Basic Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <h3 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                Basic Information
              </h3>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                {isEditing ? (
                  <Input
                    id="email"
                    type="email"
                    value={formData.email}
                    onChange={(e) =>
                      setFormData({ ...formData, email: e.target.value })
                    }
                  />
                ) : (
                  <div className="flex items-center gap-2 text-sm">
                    <Mail className="h-4 w-4 text-muted-foreground" />
                    {user.email}
                  </div>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="firstName">First Name</Label>
                  {isEditing ? (
                    <Input
                      id="firstName"
                      value={formData.firstName || ''}
                      onChange={(e) =>
                        setFormData({ ...formData, firstName: e.target.value })
                      }
                    />
                  ) : (
                    <p className="text-sm">{user.firstName || '-'}</p>
                  )}
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName">Last Name</Label>
                  {isEditing ? (
                    <Input
                      id="lastName"
                      value={formData.lastName || ''}
                      onChange={(e) =>
                        setFormData({ ...formData, lastName: e.target.value })
                      }
                    />
                  ) : (
                    <p className="text-sm">{user.lastName || '-'}</p>
                  )}
                </div>
              </div>

              {user.fullName && (
                <div className="space-y-2">
                  <Label>Full Name</Label>
                  <p className="text-sm">{user.fullName}</p>
                </div>
              )}
            </div>

            <div className="space-y-4">
              <h3 className="font-semibold text-sm text-muted-foreground uppercase tracking-wide">
                Account Details
              </h3>

              <div className="space-y-2">
                <Label>Roles</Label>
                <div className="flex flex-wrap gap-1">
                  {user.roles.map((role) => (
                    <Badge key={role} variant="secondary">
                      <Shield className="mr-1 h-3 w-3" />
                      {role}
                    </Badge>
                  ))}
                </div>
              </div>

              <div className="space-y-2">
                <Label>Created At</Label>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Calendar className="h-4 w-4" />
                  {formatDate(user.createdAt)}
                </div>
              </div>

              <div className="space-y-2">
                <Label>Last Login</Label>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Calendar className="h-4 w-4" />
                  {formatDate(user.lastLoginAt)}
                </div>
              </div>

              {user.updatedAt && (
                <div className="space-y-2">
                  <Label>Last Updated</Label>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Calendar className="h-4 w-4" />
                    {formatDate(user.updatedAt)}
                  </div>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
