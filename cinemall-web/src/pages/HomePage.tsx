import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { useAuth } from '../auth/AuthContext.tsx'

export function HomePage() {
  const { token } = useAuth()

  return (
    <div className="space-y-10">
      <div className="space-y-3">
        <p className="text-xs font-medium tracking-[0.18em] text-muted-foreground uppercase">
          Cinemall (Seen 'Em All)
        </p>
        <h1 className="font-heading text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
          Skip the queue. Seats reserved for you!
        </h1>
        <p className="max-w-xl text-base leading-relaxed text-muted-foreground md:text-lg">
          Browse movie showtimes, pick seats, and book tickets in one place.
        </p>

        <div className="flex flex-col gap-3 sm:flex-row sm:flex-wrap">
          <Button asChild>
            <Link to="/movies">Browse movies</Link>
          </Button>
          {token ? (
            <Button variant="outline" asChild>
              <Link to="/contact">Need help?</Link>
            </Button>
          ) : (
            <Button variant="outline" asChild>
              <Link to="/signup">Create account</Link>
            </Button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        <Card className="ui-surface">
          <CardHeader className="space-y-1">
            <CardTitle className="text-base">Placeholder</CardTitle>
            <CardDescription>
              Placeholder
            </CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">
            Placeholder
          </CardContent>
        </Card>

        <Card className="ui-surface">
          <CardHeader className="space-y-1">
            <CardTitle className="text-base">Placeholder</CardTitle>
            <CardDescription>
              Placeholder
            </CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">
            Placeholder
          </CardContent>
        </Card>

        <Card className="ui-surface">
          <CardHeader className="space-y-1">
            <CardTitle className="text-base">Placeholder</CardTitle>
            <CardDescription>
              Placeholder
            </CardDescription>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground">
            Placeholder
          </CardContent>
        </Card>
      </div>

      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-xl">
            {token ? "You're signed in" : 'Get started'}
          </CardTitle>
          <CardDescription>
            {token
              ? 'Movies are ready. Showtimes + seats are next.'
              : 'Sign in or create an account to continue.'}
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-3 sm:flex-row sm:flex-wrap">
          {token ? (
            <p className="text-sm text-muted-foreground">
              Thanks for testing Cinemall.
            </p>
          ) : (
            <>
              <Button asChild>
                <Link to="/signin">Sign in</Link>
              </Button>
              <Button variant="outline" asChild>
                <Link to="/about">What is Cinemall?</Link>
              </Button>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
