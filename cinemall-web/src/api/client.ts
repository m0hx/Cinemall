function apiBase(): string {
  const base = import.meta.env.VITE_API_URL
  if (base == null || base === '') {
    throw new Error('VITE_API_URL is not set')
  }
  return String(base).replace(/\/$/, '')
}

export async function postJson<T>(path: string, body: unknown): Promise<T> {
  const p = path.startsWith('/') ? path : `/${path}`
  const res = await fetch(`${apiBase()}${p}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
  const text = await res.text()
  let parsed: unknown = null
  if (text) {
    try {
      parsed = JSON.parse(text) as unknown
    } catch {
      parsed = text
    }
  }
  if (!res.ok) {
    const msg =
      typeof parsed === 'object' &&
      parsed !== null &&
      'message' in parsed &&
      typeof (parsed as { message: unknown }).message === 'string'
        ? (parsed as { message: string }).message
        : typeof parsed === 'string'
          ? parsed
          : `Request failed (${res.status})`
    throw new Error(msg)
  }
  return parsed as T
}
