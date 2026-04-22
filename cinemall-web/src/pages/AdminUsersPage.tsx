import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { getJson, patchJson } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

type UserAdmin = {
  id: number
  email: string
  displayName: string
  role: 'USER' | 'ADMIN'
  emailVerifiedAt: string | null
}

export function AdminUsersPage() {
  const { token } = useAuth()
  const [meRole, setMeRole] = useState<'USER' | 'ADMIN' | null>(null)
  const [users, setUsers] = useState<UserAdmin[]>([])
  const [error, setError] = useState<string | null>(null)
  const [savingId, setSavingId] = useState<number | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        setError(null)
        setUsers([])
        setMeRole(null)
        if (!token) return
        const me = await getJson<{ role: 'USER' | 'ADMIN' }>('/api/users/me', { token })
        if (cancelled) return
        setMeRole(me.role)
        if (me.role !== 'ADMIN') return
        const res = await getJson<UserAdmin[]>('/api/admin/users', { token })
        if (cancelled) return
        setUsers(Array.isArray(res) ? res : [])
      } catch (e) {
        if (cancelled) return
        setError(e instanceof Error ? e.message : 'Failed to load users')
      }
    })()
    return () => {
      cancelled = true
    }
  }, [token])

  if (!token) return <Navigate to="/signin" replace />
  if (meRole === 'USER') return <Navigate to="/" replace />

  async function saveUser(u: UserAdmin) {
    if (!token) return
    setError(null)
    setSavingId(u.id)
    try {
      const res = await patchJson<UserAdmin>(`/api/admin/users/${u.id}`, { displayName: u.displayName, role: u.role }, { token })
      setUsers((prev) => prev.map((x) => (x.id === u.id ? res : x)))
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to save user')
    } finally {
      setSavingId(null)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <h1 className="font-heading text-xl font-semibold">Admin · Users</h1>
        <Link className="text-sm text-violet-300 hover:underline" to="/admin">
          Back to admin
        </Link>
      </div>

      {error ? <p className="text-sm text-rose-200">{error}</p> : null}

      <div className="grid gap-3">
        {users.map((u) => (
          <Card key={u.id} className="ui-surface">
            <CardHeader className="space-y-1">
              <CardTitle className="text-base">
                {u.email} <span className="text-xs text-muted-foreground">#{u.id}</span>
              </CardTitle>
              <div className="text-xs text-muted-foreground">Role: {u.role}</div>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="grid gap-2">
                <Label>Display name</Label>
                <Input
                  value={u.displayName}
                  onChange={(e) =>
                    setUsers((prev) =>
                      prev.map((x) => (x.id === u.id ? { ...x, displayName: e.target.value } : x)),
                    )
                  }
                />
              </div>

              <div className="grid gap-2">
                <Label>Role (USER / ADMIN)</Label>
                <Input
                  value={u.role}
                  onChange={(e) =>
                    setUsers((prev) =>
                      prev.map((x) =>
                        x.id === u.id ? { ...x, role: (e.target.value.toUpperCase() as UserAdmin['role']) } : x,
                      ),
                    )
                  }
                />
              </div>

              <div className="flex items-center gap-2">
                <Button type="button" size="sm" disabled={savingId === u.id} onClick={() => void saveUser(u)}>
                  {savingId === u.id ? 'Saving…' : 'Save'}
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}

