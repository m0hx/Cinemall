/** Formats a start/end instant pair for showtime display (local timezone). */
export function formatInstantRange(startIso: string, endIso: string): string {
  const start = new Date(startIso)
  const end = new Date(endIso)
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) {
    return `${startIso} – ${endIso}`
  }
  const dateFmt = new Intl.DateTimeFormat(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
  const timeFmt = new Intl.DateTimeFormat(undefined, {
    hour: 'numeric',
    minute: '2-digit',
  })
  const sameDay =
    start.getFullYear() === end.getFullYear() &&
    start.getMonth() === end.getMonth() &&
    start.getDate() === end.getDate()
  if (sameDay) {
    return `${dateFmt.format(start)} · ${timeFmt.format(start)} – ${timeFmt.format(end)}`
  }
  return `${dateFmt.format(start)} ${timeFmt.format(start)} → ${dateFmt.format(end)} ${timeFmt.format(end)}`
}
