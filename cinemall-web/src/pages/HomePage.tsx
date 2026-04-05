import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext.tsx'

export function HomePage() {
  const { token } = useAuth()

  return (
    <div className="page">
      <h1 className="page-title">Cinemall</h1>
      <p className="page-lead">
        Login or create an account...
      </p>
      {token ? (
        <p className="page-hint">You are signed in.</p>
      ) : (
        <p className="page-hint">
          <Link to="/signin">Sign in</Link> or <Link to="/signup">create an account</Link> to
          get started.
        </p>
      )}
    </div>
  )
}
