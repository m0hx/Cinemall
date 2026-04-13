import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.tsx'
import { Button } from '@/components/ui/button'

export function Layout() {
  const { token, logout } = useAuth()

  return (
    <div className="flex min-h-svh flex-col">
      <header className="sticky top-0 z-50 border-b border-border/80 bg-background/80 backdrop-blur-md">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 md:px-6">
          <Link
            to="/"
            className="font-heading text-lg font-semibold tracking-tight text-foreground transition-colors hover:text-primary"
          >
            Cinemall
          </Link>
          <nav className="flex shrink-0 items-center gap-2">
            {token ? (
              <Button type="button" variant="outline" size="sm" onClick={logout}>
                Sign out
              </Button>
            ) : (
              <>
                <Button variant="outline" size="sm" asChild>
                  <Link to="/signin">Sign in</Link>
                </Button>
                <Button size="sm" asChild>
                  <Link to="/signup">Sign up</Link>
                </Button>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="flex-1 bg-background bg-[radial-gradient(ellipse_100%_55%_at_50%_-28%,rgb(188_172_255/0.06),transparent_52%)]">
        <div className="mx-auto w-full max-w-3xl px-4 py-10 md:px-6 md:py-14">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
