import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getJson } from '../api/client'
import { formatInstantRange } from '@/lib/formatInstantRange'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

type ShowtimeDetail = {
  id: number
  movie?: { id: number; title?: string | null } | null
  hall?: { id: number; name?: string | null } | null
  startsAt: string
  endsAt: string
  status: string
}

type HallSeat = {
  id: number
  rowLabel: string
  seatNumber: number
  seatLabel: string
  type: string
  accessible?: boolean
}

function groupSeatsByRow(seats: HallSeat[]): { rowLabel: string; seats: HallSeat[] }[] {
  const byRow = new Map<string, HallSeat[]>()
  for (const s of seats) {
    const list = byRow.get(s.rowLabel) ?? []
    list.push(s)
    byRow.set(s.rowLabel, list)
  }
  for (const list of byRow.values()) {
    list.sort((a, b) => a.seatNumber - b.seatNumber)
  }
  return Array.from(byRow.entries())
    .sort(([a], [b]) => a.localeCompare(b, undefined, { numeric: true }))
    .map(([rowLabel, rowSeats]) => ({ rowLabel, seats: rowSeats }))
}

/** Align every row to the same seat-number columns so labels line up with the grid. */
function globalSeatRange(seats: HallSeat[]): { min: number; max: number; colCount: number } {
  if (seats.length === 0) return { min: 1, max: 1, colCount: 1 }
  const nums = seats.map((s) => s.seatNumber)
  const min = Math.min(...nums)
  const max = Math.max(...nums)
  return { min, max, colCount: max - min + 1 }
}

function rowToAlignedSlots(
  rowSeats: HallSeat[],
  globalMin: number,
  globalMax: number,
): (HallSeat | null)[] {
  const len = globalMax - globalMin + 1
  const slots: (HallSeat | null)[] = Array(len).fill(null)
  for (const s of rowSeats) {
    const idx = s.seatNumber - globalMin
    if (idx >= 0 && idx < len) slots[idx] = s
  }
  return slots
}

function seatTypeIdleClass(type: string): string {
  switch (type) {
    case 'VIP':
      return 'border-amber-500/55 bg-amber-950/25 text-amber-100'
    case 'PREMIUM':
      return 'border-violet-500/45 bg-violet-950/30 text-violet-100'
    default:
      return 'border-border/80 bg-muted/30 text-foreground'
  }
}

export function ShowtimePage() {
  const { showtimeId: showtimeIdParam } = useParams<{ showtimeId: string }>()
  const showtimeId = showtimeIdParam != null ? Number(showtimeIdParam) : NaN

  const [showtime, setShowtime] = useState<ShowtimeDetail | null>(null)
  const [hallSeats, setHallSeats] = useState<HallSeat[]>([])
  const [selectedIds, setSelectedIds] = useState<Set<number>>(() => new Set())
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (Number.isNaN(showtimeId) || showtimeId < 1) {
      setError('Invalid showtime link.')
      setLoading(false)
      return
    }

    let cancelled = false
    setLoading(true)
    setError(null)
    setShowtime(null)
    setHallSeats([])
    setSelectedIds(new Set())

    ;(async () => {
      try {
        const st = await getJson<ShowtimeDetail>(`/api/showtimes/${showtimeId}`)
        if (cancelled) return
        setShowtime(st)
        const hallId = st.hall?.id
        if (hallId == null) {
          setError('This showtime has no hall assigned.')
          setLoading(false)
          return
        }
        const seats = await getJson<HallSeat[]>(`/api/halls/${hallId}/seats`)
        if (cancelled) return
        setHallSeats(Array.isArray(seats) ? seats : [])
      } catch (err) {
        if (cancelled) return
        setError(err instanceof Error ? err.message : 'Failed to load showtime')
      } finally {
        if (!cancelled) setLoading(false)
      }
    })()

    return () => {
      cancelled = true
    }
  }, [showtimeId])

  const rows = useMemo(() => groupSeatsByRow(hallSeats), [hallSeats])
  const { min: seatColMin, max: seatColMax, colCount } = useMemo(
    () => globalSeatRange(hallSeats),
    [hallSeats],
  )

  const alignedRows = useMemo(() => {
    return rows.map((r) => ({
      rowLabel: r.rowLabel,
      slots: rowToAlignedSlots(r.seats, seatColMin, seatColMax),
    }))
  }, [rows, seatColMin, seatColMax])

  function toggleSeat(id: number) {
    setSelectedIds((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  if (error && !loading) {
    return (
      <div className="space-y-4">
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Couldn’t open this showtime</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-wrap gap-2">
            <Button asChild variant="outline">
              <Link to="/movies">Browse movies</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  const movieId = showtime?.movie?.id
  const movieTitle = showtime?.movie?.title ?? 'Movie'

  const seatNumberHeaders = useMemo(() => {
    const list: number[] = []
    for (let n = seatColMin; n <= seatColMax; n++) list.push(n)
    return list
  }, [seatColMin, seatColMax])

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
        <Link to="/movies" className="text-violet-300 hover:underline">
          Movies
        </Link>
        {movieId != null ? (
          <>
            <span aria-hidden>/</span>
            <Link to={`/movies/${movieId}`} className="text-violet-300 hover:underline">
              {movieTitle}
            </Link>
          </>
        ) : null}
        <span aria-hidden>/</span>
        <span className="text-foreground">Seats</span>
      </div>

      {loading ? (
        <Card className="ui-surface">
          <CardHeader className="space-y-2">
            <div className="h-6 w-2/3 rounded bg-muted/40" />
            <div className="h-4 w-full max-w-md rounded bg-muted/30" />
          </CardHeader>
        </Card>
      ) : showtime ? (
        <>
          <Card className="ui-surface">
            <CardHeader>
              <CardTitle className="text-lg">{movieTitle}</CardTitle>
              <div className="space-y-1 text-sm text-muted-foreground">
                <p>
                  <span className="font-medium text-foreground/85">When:</span>{' '}
                  {formatInstantRange(showtime.startsAt, showtime.endsAt)}
                </p>
                <p>
                  <span className="font-medium text-foreground/85">Hall:</span>{' '}
                  {showtime.hall?.name ?? `Hall #${showtime.hall?.id ?? '?'}`}
                </p>
                <p>
                  <span className="font-medium text-foreground/85">Status:</span>{' '}
                  {showtime.status}
                </p>
              </div>
            </CardHeader>
            <CardContent className="text-xs text-muted-foreground">
              Seat numbers share one column index across all rows. Empty cells are aisles / gaps. Booking
              API comes later.
            </CardContent>
          </Card>

          <Card className="ui-surface overflow-hidden">
            <CardHeader>
              <CardTitle className="text-lg">Select seats</CardTitle>
              <CardDescription>
                Tap a seat to toggle. Selected seats show a checkmark.{' '}
                <span className="text-foreground/90">
                  {selectedIds.size > 0 ? `${selectedIds.size} selected` : 'None selected'}
                </span>
                .
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-5 px-2 sm:px-6">
              {/* Screen */}
              <div
                className="mx-auto max-w-3xl rounded-lg border border-border/50 bg-gradient-to-b from-muted/50 to-muted/10 px-4 py-3 text-center shadow-inner"
                role="presentation"
              >
                <p className="text-[0.65rem] font-semibold uppercase tracking-[0.35em] text-muted-foreground">
                  Screen
                </p>
                <div className="mx-auto mt-1 h-1 max-w-[min(100%,20rem)] rounded-full bg-gradient-to-r from-transparent via-violet-500/40 to-transparent" />
              </div>

              {/* Horizontally scrollable grid — keeps alignment on narrow phones */}
              <div className="-mx-2 overflow-x-auto pb-1 sm:mx-0 [scrollbar-gutter:stable]">
                <div
                  className="inline-block min-w-[min(100%,calc(100vw-2rem))] px-2 sm:min-w-0 sm:w-full sm:px-0"
                  style={{
                    minWidth: `max(100%, calc(2.75rem + ${colCount} * 2.875rem))`,
                  }}
                >
                  <div
                    className="grid w-full gap-x-1 gap-y-2"
                    style={{
                      gridTemplateColumns: `2.75rem repeat(${colCount}, minmax(2.75rem, 1fr))`,
                    }}
                  >
                    {/* Corner + seat number headers */}
                    <div className="sticky left-0 z-20 bg-card pb-1 text-[0.65rem] font-medium uppercase tracking-wide text-muted-foreground">
                      Row
                    </div>
                    {seatNumberHeaders.map((n) => (
                      <div
                        key={n}
                        className="pb-1 text-center text-[0.65rem] font-semibold tabular-nums text-muted-foreground"
                      >
                        {n}
                      </div>
                    ))}

                    {alignedRows.map(({ rowLabel, slots }) => (
                      <div key={rowLabel} className="contents">
                        <div
                          className="sticky left-0 z-10 flex h-11 items-center justify-center rounded-md border border-transparent bg-card pr-1 text-sm font-semibold tabular-nums text-muted-foreground shadow-[4px_0_12px_-4px_rgba(0,0,0,0.45)]"
                          aria-hidden
                        >
                          {rowLabel}
                        </div>
                        {slots.map((slot, idx) => {
                          if (slot == null) {
                            return (
                              <div
                                key={`gap-${rowLabel}-${idx}`}
                                className="h-11 rounded-md bg-muted/5"
                                aria-hidden
                              />
                            )
                          }
                          const s = slot
                          const selected = selectedIds.has(s.id)
                          const idle = seatTypeIdleClass(s.type)
                          return (
                            <button
                              key={s.id}
                              type="button"
                              aria-pressed={selected}
                              aria-label={`Seat ${s.seatLabel}, ${s.type}${s.accessible ? ', accessible' : ''}${selected ? ', selected' : ', available'}`}
                              title={`${s.seatLabel} · ${s.type}`}
                              onClick={() => toggleSeat(s.id)}
                              className={cn(
                                'relative flex h-11 min-h-[44px] min-w-[44px] flex-col items-center justify-center rounded-lg border-2 text-[0.7rem] font-semibold leading-none transition-colors sm:h-11 sm:min-w-[2.75rem]',
                                'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-violet-400/60',
                                selected
                                  ? 'border-violet-500/80 bg-violet-600 text-white shadow-[inset_0_0_0_1px_rgba(255,255,255,0.2)]'
                                  : cn('active:brightness-95', idle, 'hover:brightness-110'),
                              )}
                            >
                              <span className="tabular-nums">{s.seatNumber}</span>
                              {selected ? (
                                <span
                                  className="absolute right-0.5 top-0.5 flex h-3.5 w-3.5 items-center justify-center rounded-full bg-white/90 text-[0.55rem] font-bold text-violet-700"
                                  aria-hidden
                                >
                                  ✓
                                </span>
                              ) : null}
                            </button>
                          )
                        })}
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <p className="text-center text-[0.65rem] text-muted-foreground sm:text-xs">
                Scroll sideways on small screens. Row letters are fixed on the left while you scroll.
              </p>

              {/* Legend */}
              <div className="flex flex-wrap items-center justify-center gap-3 border-t border-border/50 pt-4 text-[0.7rem] text-muted-foreground sm:text-xs">
                <span className="font-medium text-foreground/80">Key:</span>
                <span className="flex items-center gap-1.5">
                  <span className="h-4 w-4 rounded border-2 border-border/80 bg-muted/30" /> Standard
                </span>
                <span className="flex items-center gap-1.5">
                  <span className="h-4 w-4 rounded border-2 border-violet-500/50 bg-violet-950/30" />{' '}
                  Premium
                </span>
                <span className="flex items-center gap-1.5">
                  <span className="h-4 w-4 rounded border-2 border-amber-500/55 bg-amber-950/25" /> VIP
                </span>
                <span className="flex items-center gap-1.5">
                  <span className="inline-flex h-4 w-4 items-center justify-center rounded border-2 border-violet-500/80 bg-violet-600 text-[0.55rem] text-white shadow-[inset_0_0_0_1px_rgba(255,255,255,0.2)]">
                    ✓
                  </span>{' '}
                  Selected
                </span>
              </div>
            </CardContent>
          </Card>
        </>
      ) : null}
    </div>
  )
}
