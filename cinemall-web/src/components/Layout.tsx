import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.tsx'
import './Layout.css'

export function Layout() {
  const { token, logout } = useAuth()

  return (
    <div className="layout">
      <header className="layout-header">
        <Link to="/" className="layout-brand">
          Cinemall
        </Link>
        <nav className="layout-nav">
          {token ? (
            <button type="button" className="btn-ghost" onClick={logout}>
              Sign out
            </button>
          ) : (
            <>
              <Link to="/signin">Sign in</Link>
              <Link to="/signup" className="layout-nav-cta">
                Sign up
              </Link>
            </>
          )}
        </nav>
      </header>
      <main className="layout-main">
        <Outlet />
      </main>
    </div>
  )
}
