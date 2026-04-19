import { Link, useParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

export function ShowtimePlaceholderPage() {
  const { showtimeId } = useParams<{ showtimeId: string }>()

  return (
    <div className="space-y-6">
      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-lg">Showtime {showtimeId ?? '—'}</CardTitle>
          <CardDescription>
            Seat map and booking flow are not built yet. This page confirms routing from the
            movie detail screen.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-2">
          <Button asChild variant="outline">
            <Link to="/movies">Browse movies</Link>
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
