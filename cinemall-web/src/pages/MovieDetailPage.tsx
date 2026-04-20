import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getBlobUrl, getJson } from '../api/client'
import { formatInstantRange } from '@/lib/formatInstantRange'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

type Genre = {
  id: number
  name: string
}

type Movie = {
  id: number
  title: string
  description?: string | null
  releaseDate?: string | null
  durationMins?: number | null
  status?: string | null
  genre?: Genre | null
}

type Showtime = {
  id: number
  hall?: { id: number; name?: string | null } | null
  startsAt: string
  endsAt: string
  status: string
}

export function MovieDetailPage() {
  const { movieId: movieIdParam } = useParams<{ movieId: string }>()
  const navigate = useNavigate()
  const movieId = movieIdParam != null ? Number(movieIdParam) : NaN

  const [movie, setMovie] = useState<Movie | null>(null)
  const [showtimes, setShowtimes] = useState<Showtime[]>([])
  const [posterUrl, setPosterUrl] = useState<string | null>(null)
  const posterUrlRef = useRef<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (Number.isNaN(movieId) || movieId < 1) {
      setError('Invalid movie link.')
      setLoading(false)
      return
    }

    let cancelled = false
    setLoading(true)
    setError(null)
    setMovie(null)
    setShowtimes([])

    Promise.all([
      getJson<Movie>(`/api/movies/${movieId}`),
      getJson<Showtime[]>(`/api/movies/${movieId}/showtimes`),
    ])
      .then(([movieData, showtimesData]) => {
        if (cancelled) return
        setMovie(movieData)
        setShowtimes(Array.isArray(showtimesData) ? showtimesData : [])
      })
      .catch((err) => {
        if (cancelled) return
        setError(err instanceof Error ? err.message : 'Failed to load movie')
      })
      .finally(() => {
        if (cancelled) return
        setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [movieId])

  useEffect(() => {
    if (Number.isNaN(movieId) || movieId < 1) return

    let cancelled = false

    async function loadPoster() {
      try {
        const url = await getBlobUrl(`/api/movies/${movieId}/poster`)
        if (cancelled) {
          URL.revokeObjectURL(url)
          return
        }
        const old = posterUrlRef.current
        if (old) URL.revokeObjectURL(old)
        posterUrlRef.current = url
        setPosterUrl(url)
      } catch {
        if (!cancelled) {
          const old = posterUrlRef.current
          if (old) URL.revokeObjectURL(old)
          posterUrlRef.current = null
          setPosterUrl(null)
        }
      }
    }

    void loadPoster()

    return () => {
      cancelled = true
    }
  }, [movieId])

  useEffect(() => {
    return () => {
      const u = posterUrlRef.current
      if (u) URL.revokeObjectURL(u)
      posterUrlRef.current = null
    }
  }, [])

  if (error && !loading) {
    return (
      <div className="space-y-4">
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Couldn’t open this movie</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-wrap gap-2">
            <Button asChild variant="outline">
              <Link to="/movies">Back to movies</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-2 text-sm text-muted-foreground">
        <Link to="/movies" className="text-violet-300 hover:underline">
          Movies
        </Link>
        <span aria-hidden>/</span>
        <span className="text-foreground">
          {movie?.title ?? (loading ? 'Loading…' : 'Movie')}
        </span>
      </div>

      {loading ? (
        <Card className="ui-surface">
          <CardHeader className="space-y-2">
            <div className="h-6 w-1/2 rounded bg-muted/40" />
            <div className="h-4 w-1/3 rounded bg-muted/30" />
          </CardHeader>
          <CardContent>
            <div className="aspect-[16/9] max-h-80 w-full rounded-md bg-muted/20 sm:max-w-md" />
          </CardContent>
        </Card>
      ) : movie ? (
        <div className="grid gap-6 lg:grid-cols-[minmax(0,280px)_1fr]">
          <div className="space-y-3">
            <div className="aspect-[3/4] overflow-hidden rounded-lg border border-border/60 bg-muted/10">
              {posterUrl ? (
                <img
                  src={posterUrl}
                  alt={`${movie.title} poster`}
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center px-4 text-center text-sm text-muted-foreground">
                  No poster
                </div>
              )}
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <h1 className="font-heading text-3xl font-semibold tracking-tight text-foreground">
                {movie.title}
              </h1>
              <p className="mt-2 flex flex-wrap items-center gap-x-2 gap-y-1 text-sm text-muted-foreground">
                <span>
                  <span className="font-medium text-foreground/85">Genre:</span>{' '}
                  {movie.genre?.name ?? 'Uncategorized'}
                </span>
                {movie.durationMins != null ? (
                  <>
                    <span aria-hidden className="text-border/80">
                      |
                    </span>
                    <span>
                      <span className="font-medium text-foreground/85">Duration:</span>{' '}
                      {movie.durationMins} min
                    </span>
                  </>
                ) : null}
                {movie.releaseDate ? (
                  <>
                    <span aria-hidden className="text-border/80">
                      |
                    </span>
                    <span>
                      <span className="font-medium text-foreground/85">Release date:</span>{' '}
                      {movie.releaseDate}
                    </span>
                  </>
                ) : null}
              </p>
            </div>

            {movie.description ? (
              <p className="text-sm leading-relaxed text-muted-foreground md:text-base">
                {movie.description}
              </p>
            ) : (
              <p className="text-sm text-muted-foreground">No description yet.</p>
            )}

            <Card className="ui-surface">
              <CardHeader>
                <CardTitle className="text-lg">Showtimes</CardTitle>
                <CardDescription>
                  Choose a time. Seat selection is the next milestone.
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-3">
                {showtimes.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    No showtimes scheduled for this movie yet.
                  </p>
                ) : (
                  <ul className="space-y-2">
                    {showtimes.map((s) => (
                      <li
                        key={s.id}
                        className="flex flex-col gap-2 rounded-md border border-border/50 bg-background/40 px-3 py-3 sm:flex-row sm:items-center sm:justify-between"
                      >
                        <div>
                          <p className="text-sm font-medium text-foreground">
                            {formatInstantRange(s.startsAt, s.endsAt)}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {s.hall?.name ?? `Hall #${s.hall?.id ?? '?'}`} · {s.status}
                          </p>
                        </div>
                        <Button
                          type="button"
                          size="sm"
                          className="shrink-0"
                          onClick={() => navigate(`/showtimes/${s.id}`)}
                        >
                          Select showtime
                        </Button>
                      </li>
                    ))}
                  </ul>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      ) : null}
    </div>
  )
}
