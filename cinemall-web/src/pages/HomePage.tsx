import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useAuth } from "../auth/AuthContext.tsx";

export function HomePage() {
  const { token } = useAuth();

  return (
    <div className="space-y-8">
      <div className="space-y-2">
        <p className="text-xs font-medium tracking-[0.18em] text-muted-foreground uppercase">
          Cinemall (Seen 'Em All)
        </p>
        <h1 className="font-heading text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
          Skip the queue. Seats reserved for you.
        </h1>
        <p className="max-w-xl text-base leading-relaxed text-muted-foreground md:text-lg">
          Browse movie showtimes, pick seats, and book tickets in one place.
        </p>
      </div>

      <Card className="ui-surface">
        <CardHeader>
          <CardTitle className="text-xl">
            {token ? "You're signed in" : "Get started"}
          </CardTitle>
          <CardDescription>
            {token
              ? "Movies and showtimes are coming soon."
              : "Sign in or create an account to continue."}
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
                <Link to="/signup">Create account</Link>
              </Button>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
