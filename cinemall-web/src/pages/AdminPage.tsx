import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { getJson } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Me = { role: 'USER' | 'ADMIN' }

export function AdminPage() {
  const { token } = useAuth()
  const [role, setRole] = useState<Me['role'] | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      setRole(null)
      if (!token) return
      try {
        const me = await getJson<Me>('/api/users/me', { token })
        if (!cancelled) setRole(me.role)
      } catch {
        if (!cancelled) setRole('USER')
      }
    })()
    return () => {
      cancelled = true
    }
  }, [token])

  if (!token) return <Navigate to="/signin" replace />
  if (role === 'USER') return <Navigate to="/" replace />

  return (
    <div className="space-y-4">
      <h1 className="font-heading text-xl font-semibold">Admin</h1>

      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Users</CardTitle>
          </CardHeader>
          <CardContent>
            <Link className="text-violet-300 hover:underline" to="/admin/users">
              Manage users
            </Link>
          </CardContent>
        </Card>

        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Bookings</CardTitle>
          </CardHeader>
          <CardContent>
            <Link className="text-violet-300 hover:underline" to="/admin/bookings">
              View all bookings
            </Link>
          </CardContent>
        </Card>

        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Catalog</CardTitle>
          </CardHeader>
          <CardContent className="space-y-1 text-sm">
            <Link className="block text-violet-300 hover:underline" to="/movies">
              Movies (admin actions on pages)
            </Link>
            <Link className="block text-violet-300 hover:underline" to="/movies">
              Showtimes (admin via API)
            </Link>
            <Link className="block text-violet-300 hover:underline" to="/movies">
              Genres (admin via API)
            </Link>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

