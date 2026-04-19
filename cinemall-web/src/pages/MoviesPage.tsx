import { useEffect, useMemo, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { getBlobUrl, getJson } from '../api/client'
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

export function MoviesPage() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [genres, setGenres] = useState<Genre[]>([])
  const [selectedGenreId, setSelectedGenreId] = useState<number | 'all'>('all')
  const [posters, setPosters] = useState<Record<number, string>>({})
  const posterUrlsRef = useRef<Record<number, string>>({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)

    Promise.all([
      getJson<Movie[]>('/api/movies'),
      getJson<Genre[]>('/api/genres'),
    ])
      .then(([moviesData, genresData]) => {
        if (cancelled) return
        setMovies(Array.isArray(moviesData) ? moviesData : [])
        setGenres(Array.isArray(genresData) ? genresData : [])
      })
      .catch((err) => {
        if (cancelled) return
        setError(err instanceof Error ? err.message : 'Failed to load movies')
      })
      .finally(() => {
        if (cancelled) return
        setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  const filteredMovies = useMemo(() => {
    if (selectedGenreId === 'all') return movies
    return movies.filter((m) => m.genre?.id === selectedGenreId)
  }, [movies, selectedGenreId])

  useEffect(() => {
    let cancelled = false

    async function loadPosters() {
      const missing = filteredMovies
        .map((m) => m.id)
        .filter((id) => posterUrlsRef.current[id] == null)

      if (missing.length === 0) return

      for (const id of missing) {
        try {
          const url = await getBlobUrl(`/api/movies/${id}/poster`)
          if (cancelled) {
            URL.revokeObjectURL(url)
            continue
          }
          const old = posterUrlsRef.current[id]
          if (old) URL.revokeObjectURL(old)
          posterUrlsRef.current[id] = url
          setPosters((prev) => ({ ...prev, [id]: url }))
        } catch {
          // No poster for this movie yet (or endpoint returns 404). Skip silently.
        }
      }
    }

    void loadPosters()

    return () => {
      cancelled = true
    }
  }, [filteredMovies])

  useEffect(() => {
    return () => {
      for (const url of Object.values(posterUrlsRef.current)) {
        URL.revokeObjectURL(url)
      }
      posterUrlsRef.current = {}
    }
  }, [])

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div className="space-y-1">
          <p className="text-xs font-medium tracking-[0.18em] text-muted-foreground uppercase">
            Browse
          </p>
          <h1 className="font-heading text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
            Movies
          </h1>
          <p className="max-w-2xl text-sm text-muted-foreground md:text-base">
            Pick a movie to see available showtimes. (Halls + seats are next.)
          </p>
        </div>

        <div className="flex flex-col gap-2 sm:items-end">
          <label className="text-xs font-medium text-muted-foreground" htmlFor="genre-filter">
            Filter by genre
          </label>
          <select
            id="genre-filter"
            className="h-9 w-full rounded-md border border-border/70 bg-background px-3 text-sm text-foreground shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring sm:w-56"
            value={selectedGenreId === 'all' ? 'all' : String(selectedGenreId)}
            onChange={(e) => {
              const v = e.target.value
              setSelectedGenreId(v === 'all' ? 'all' : Number(v))
            }}
          >
            <option value="all">All genres</option>
            {genres.map((g) => (
              <option key={g.id} value={String(g.id)}>
                {g.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error ? (
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Couldn’t load movies</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button asChild variant="outline">
              <Link to="/">Back home</Link>
            </Button>
          </CardContent>
        </Card>
      ) : null}

      {loading ? (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {Array.from({ length: 4 }).map((_, idx) => (
            <Card key={idx} className="ui-surface">
              <CardHeader className="space-y-2">
                <div className="h-4 w-2/3 rounded bg-muted/40" />
                <div className="h-3 w-1/3 rounded bg-muted/30" />
              </CardHeader>
              <CardContent>
                <div className="aspect-[3/4] w-full rounded-md bg-muted/20" />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {filteredMovies.map((m) => {
            const posterUrl = posters[m.id]
            return (
              <Card key={m.id} className="ui-surface overflow-hidden">
                <CardHeader className="space-y-1">
                  <CardTitle className="text-lg">{m.title}</CardTitle>
                  <CardDescription>
                    {m.genre?.name ? m.genre.name : 'Uncategorized'}
                    {m.durationMins ? ` • ${m.durationMins} min` : ''}
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="aspect-[3/4] w-full overflow-hidden rounded-md border border-border/60 bg-muted/10">
                    {posterUrl ? (
                      <img
                        src={posterUrl}
                        alt={`${m.title} poster`}
                        className="h-full w-full object-cover"
                        loading="lazy"
                      />
                    ) : (
                      <div className="flex h-full w-full items-center justify-center px-4 text-center text-xs text-muted-foreground">
                        Poster coming soon
                      </div>
                    )}
                  </div>
                  {m.description ? (
                    <p className="line-clamp-3 text-sm text-muted-foreground">
                      {m.description}
                    </p>
                  ) : null}
                  <Button className="w-full" asChild>
                    <Link to={`/movies/${m.id}`}>View details & showtimes</Link>
                  </Button>
                </CardContent>
              </Card>
            )
          })}
        </div>
      )}

      {!loading && !error && filteredMovies.length === 0 ? (
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">No movies found</CardTitle>
            <CardDescription>Try a different genre filter.</CardDescription>
          </CardHeader>
        </Card>
      ) : null}
    </div>
  )
}

