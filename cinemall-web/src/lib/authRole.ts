export function jwtRole(token: string | null): 'ADMIN' | 'USER' | null {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length < 2) return null
  try {
    const payloadJson = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'))
    const payload = JSON.parse(payloadJson) as unknown
    if (
      typeof payload === 'object' &&
      payload !== null &&
      'authorities' in payload &&
      Array.isArray((payload as { authorities: unknown }).authorities)
    ) {
      const a = (payload as { authorities: string[] }).authorities
      if (a.includes('ADMIN')) return 'ADMIN'
      if (a.includes('USER')) return 'USER'
    }
  } catch {
    return null
  }
  return null
}

