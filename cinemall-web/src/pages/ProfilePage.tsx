import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getJson, patchJson } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

type Me = {
  id: number
  email: string
  displayName: string
  role: 'USER' | 'ADMIN'
  emailVerifiedAt: string | null
}

export function ProfilePage() {
  const { token } = useAuth()
  const [me, setMe] = useState<Me | null>(null)
  const [displayName, setDisplayName] = useState('')
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [savedMsg, setSavedMsg] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        setError(null)
        setSavedMsg(null)
        setMe(null)
        setDisplayName('')
        setCurrentPassword('')
        setNewPassword('')

        if (!token) return
        const res = await getJson<Me>('/api/users/me', { token })
        if (cancelled) return
        setMe(res)
        setDisplayName(res.displayName ?? '')
      } catch (e) {
        if (cancelled) return
        setError(e instanceof Error ? e.message : 'Failed to load profile')
      }
    })()

    return () => {
      cancelled = true
    }
  }, [token])

  async function save() {
    setError(null)
    setSavedMsg(null)
    if (!token) return
    setSaving(true)
    try {
      const body: { displayName?: string; currentPassword?: string; newPassword?: string } = {
        displayName,
      }
      if (newPassword.trim().length > 0) {
        body.currentPassword = currentPassword
        body.newPassword = newPassword
      }
      const res = await patchJson<Me>('/api/users/me', body, { token })
      setMe(res)
      setDisplayName(res.displayName ?? '')
      setCurrentPassword('')
      setNewPassword('')
      setSavedMsg('Saved.')
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to save profile')
    } finally {
      setSaving(false)
    }
  }

  if (!token) {
    return (
      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-base">Profile</CardTitle>
        </CardHeader>
        <CardContent className="text-sm text-muted-foreground">
          Please <Link className="text-violet-300 hover:underline" to="/signin">sign in</Link>.
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between gap-3">
        <h1 className="font-heading text-xl font-semibold">Profile</h1>
        <Link className="text-sm text-violet-300 hover:underline" to="/bookings">
          My bookings
        </Link>
      </div>

      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-base">Account</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {error ? <p className="text-sm text-rose-200">{error}</p> : null}
          {savedMsg ? <p className="text-sm text-emerald-200">{savedMsg}</p> : null}

          <div className="grid gap-2">
            <Label>Email</Label>
            <Input value={me?.email ?? ''} disabled />
          </div>

          <div className="grid gap-2">
            <Label>Display name</Label>
            <Input value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
          </div>

          <div className="grid gap-2">
            <Label>Current password</Label>
            <Input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>

          <div className="grid gap-2">
            <Label>New password</Label>
            <Input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>

          <div className="flex items-center gap-2 pt-2">
            <Button type="button" disabled={saving} onClick={() => void save()}>
              {saving ? 'Saving…' : 'Save changes'}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

