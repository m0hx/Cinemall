import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { postJson } from '../api/client.ts'
import { useAuth } from '../auth/AuthContext.tsx'

type LoginResponse = { message: string }

function isLoginError(message: string) {
  return message.startsWith('Error')
}

export function SignInPage() {
  const navigate = useNavigate()
  const { setToken } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = await postJson<LoginResponse>('/auth/users/login', {
        email,
        password,
      })
      if (isLoginError(data.message)) {
        setError(data.message.replace(/^Error\s*:\s*/i, '').trim() || data.message)
        return
      }
      setToken(data.message)
      navigate('/')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Sign in failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <h1 className="page-title">Sign in</h1>
      <form className="form" onSubmit={onSubmit}>
        <label className="field">
          <span>Email</span>
          <input
            type="email"
            autoComplete="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label className="field">
          <span>Password</span>
          <input
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        {error ? <p className="form-error">{error}</p> : null}
        <button type="submit" className="form-submit" disabled={loading}>
          {loading ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
      <p className="page-footer">
        No account? <Link to="/signup">Sign up</Link>
      </p>
    </div>
  )
}
