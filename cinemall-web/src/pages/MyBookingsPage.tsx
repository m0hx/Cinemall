import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getJson } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type BookingSummary = {
  id: number
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED'
  createdAt: string
  confirmedAt: string | null
  showtimeId: number
  startsAt: string
  endsAt: string
  movieTitle: string | null
  seatLabels: string[]
}

function fmt(iso: string): string {
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

export function MyBookingsPage() {
  const { token } = useAuth()
  const [bookings, setBookings] = useState<BookingSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        if (!token) {
          setBookings([])
          return
        }
        const res = await getJson<BookingSummary[]>('/api/bookings', { token })
        if (cancelled) return
        setBookings(Array.isArray(res) ? res : [])
      } catch (e) {
        if (cancelled) return
        setError(e instanceof Error ? e.message : 'Failed to load bookings')
      } finally {
        if (!cancelled) setLoading(false)
      }
    })()

    return () => {
      cancelled = true
    }
  }, [token])

  if (!token) {
    return (
      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-base">My bookings</CardTitle>
        </CardHeader>
        <CardContent className="text-sm text-muted-foreground">
          Please <Link className="text-violet-300 hover:underline" to="/signin">sign in</Link> to see your bookings.
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <h1 className="font-heading text-xl font-semibold">My bookings</h1>
        <Link className="text-sm text-violet-300 hover:underline" to="/movies">
          Browse movies
        </Link>
      </div>

      {loading ? (
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Loading…</CardTitle>
          </CardHeader>
        </Card>
      ) : error ? (
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Couldn’t load bookings</CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">{error}</CardContent>
        </Card>
      ) : bookings.length === 0 ? (
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">No bookings yet</CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">
            Reserve seats on a showtime, then pay & confirm.
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-3">
          {bookings.map((b) => (
            <Card key={b.id} className="ui-surface">
              <CardHeader className="space-y-1">
                <CardTitle className="text-base">
                  {b.movieTitle ?? 'Movie'}{' '}
                  <span className="text-xs font-medium text-muted-foreground">#{b.id}</span>
                </CardTitle>
                <div className="text-xs text-muted-foreground">
                  <span className="font-medium text-foreground/80">Status:</span> {b.status} ·{' '}
                  <span className="font-medium text-foreground/80">Created:</span> {fmt(b.createdAt)}
                </div>
              </CardHeader>
              <CardContent className="space-y-2 text-sm">
                <div className="text-muted-foreground">
                  <span className="font-medium text-foreground/80">Showtime:</span> {fmt(b.startsAt)} –{' '}
                  {fmt(b.endsAt)}
                </div>
                <div className="text-muted-foreground">
                  <span className="font-medium text-foreground/80">Seats:</span>{' '}
                  {b.seatLabels.length ? b.seatLabels.join(', ') : '—'}
                </div>
                <div className="pt-1">
                  <Link className="text-violet-300 hover:underline" to={`/showtimes/${b.showtimeId}`}>
                    View showtime
                  </Link>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

