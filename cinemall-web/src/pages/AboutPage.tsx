import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export function AboutPage() {
  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <p className="text-xs font-medium tracking-[0.18em] text-muted-foreground uppercase">
          About
        </p>
        <h1 className="font-heading text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
          Cinemall
        </h1>
        <p className="max-w-2xl text-sm text-muted-foreground md:text-base">
          Cinemall is a cinema tickets booking app: browse movies, choose a showtime, pick seats, and checkout.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Placeholder</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm text-muted-foreground">
            <ul className="list-inside list-disc space-y-1">
              <li>Placeholder</li>
              <li>Placeholder</li>
              <li>Placeholder</li>
            </ul>
          </CardContent>
        </Card>

        <Card className="ui-surface">
          <CardHeader>
            <CardTitle className="text-base">Placeholdert</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm text-muted-foreground">
            <ul className="list-inside list-disc space-y-1">
              <li>Placeholder</li>
              <li>Placeholder</li>
              <li>Placeholder</li>
            </ul>
          </CardContent>
        </Card>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row">
        <Button asChild>
          <Link to="/movies">Browse movies</Link>
        </Button>
        <Button variant="outline" asChild>
          <Link to="/contact">Contact</Link>
        </Button>
      </div>
    </div>
  )
}

