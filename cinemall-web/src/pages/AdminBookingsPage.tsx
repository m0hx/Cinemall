import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { getJson } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type BookingAdmin = {
  id: number
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED'
  createdAt: string
  confirmedAt: string | null
  userId: number | null
  userEmail: string | null
  showtimeId: number | null
  startsAt: string | null
  endsAt: string | null
  movieTitle: string | null
  seatLabels: string[]
}

function fmt(iso: string | null): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return Number.isNaN(d.getTime())
    ? iso
    : new Intl.DateTimeFormat(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
      }).format(d)
}

export function AdminBookingsPage() {
  const { token } = useAuth()
  const [meRole, setMeRole] = useState<'USER' | 'ADMIN' | null>(null)
  const [bookings, setBookings] = useState<BookingAdmin[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        setError(null)
        setBookings([])
        setMeRole(null)
        if (!token) return
        const me = await getJson<{ role: 'USER' | 'ADMIN' }>('/api/users/me', { token })
        if (cancelled) return
        setMeRole(me.role)
        if (me.role !== 'ADMIN') return
        const res = await getJson<BookingAdmin[]>('/api/admin/bookings', { token })
        if (cancelled) return
        setBookings(Array.isArray(res) ? res : [])
      } catch (e) {
        if (cancelled) return
        setError(e instanceof Error ? e.message : 'Failed to load bookings')
      }
    })()
    return () => {
      cancelled = true
    }
  }, [token])

  if (!token) return <Navigate to="/signin" replace />
  if (meRole === 'USER') return <Navigate to="/" replace />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <h1 className="font-heading text-xl font-semibold">Admin · Bookings</h1>
        <Link className="text-sm text-violet-300 hover:underline" to="/admin">
          Back to admin
        </Link>
      </div>

      {error ? <p className="text-sm text-rose-200">{error}</p> : null}

      <div className="grid gap-3">
        {bookings.map((b) => (
          <Card key={b.id} className="ui-surface">
            <CardHeader className="space-y-1">
              <CardTitle className="text-base">
                {b.movieTitle ?? 'Movie'}{' '}
                <span className="text-xs font-medium text-muted-foreground">#{b.id}</span>
              </CardTitle>
              <div className="text-xs text-muted-foreground">
                {b.status} · {b.userEmail ?? '—'}
              </div>
            </CardHeader>
            <CardContent className="space-y-2 text-sm text-muted-foreground">
              <div>
                <span className="font-medium text-foreground/80">Created:</span> {fmt(b.createdAt)} ·{' '}
                <span className="font-medium text-foreground/80">Confirmed:</span> {fmt(b.confirmedAt)}
              </div>
              <div>
                <span className="font-medium text-foreground/80">Showtime:</span> {fmt(b.startsAt)} – {fmt(b.endsAt)}
              </div>
              <div>
                <span className="font-medium text-foreground/80">Seats:</span>{' '}
                {b.seatLabels.length ? b.seatLabels.join(', ') : '—'}
              </div>
              {b.showtimeId != null ? (
                <div className="pt-1">
                  <Link className="text-violet-300 hover:underline" to={`/showtimes/${b.showtimeId}`}>
                    View showtime
                  </Link>
                </div>
              ) : null}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}

