# Cinemall

A modern cinema tickets booking system with real-time seat reservations, notifications, and secure payments.

## Quick start

| Type       | URL |
|------------|------|
| **API**    | http://localhost:8080 |
| **Web UI** | http://localhost:5173 |

# 🌐 REST API Endpoints 
___
## 🔒 Auth
| Request Type | URL                      | Functionality   | Access  |
|--------------|--------------------------|-----------------|---------|
| POST         | /auth/users/login        | 🔑 User login   | Public  |
| POST         | /auth/users/register     | 📝 Register     | Public  |
## 🎬 Movies
| Request Type | URL                          | Functionality            | Access  |
|--------------|------------------------------|--------------------------|---------|
| POST         | /api/movies                  | ➕ Create movie          | Private |
| GET          | /api/movies                  | 📃 Get all movies        | Public  |
| GET          | /api/movies/{id}             | 🔍 Get movie by ID       | Public  |
| GET          | /api/movies/{id}/poster      | 🖼️ Get movie poster      | Public  |
| PUT          | /api/movies/{id}             | ✏️ Update movie          | Private |
| PUT          | /api/movies/{id}/poster      | 📤 Upload poster         | Private |
| DELETE       | /api/movies/{id}             | ❌ Delete movie          | Private |
## 🎭 Genres
| Request Type | URL                      | Functionality        | Access  |
|--------------|--------------------------|----------------------|---------|
| POST         | /api/genres              | ➕ Create genre      | Private |
| GET          | /api/genres              | 📃 Get all genres    | Public  |
| GET          | /api/genres/{id}         | 🔍 Get genre by ID   | Public  |
| PUT          | /api/genres/{id}         | ✏️ Update genre      | Private |
| DELETE       | /api/genres/{id}         | ❌ Delete genre      | Private |
## 🏟️ Halls
| Request Type | URL                              | Functionality              | Access  |
|--------------|----------------------------------|----------------------------|---------|
| GET          | /api/halls                       | 📃 Get all halls           | Public  |
| GET          | /api/halls/{id}                  | 🔍 Get hall by ID          | Public  |
| POST         | /api/halls                       | ➕ Create hall             | Private |
| PUT          | /api/halls/{id}                  | ✏️ Update hall             | Private |
| DELETE       | /api/halls/{id}                  | ❌ Delete hall             | Private |
| GET          | /api/halls/{id}/seats            | 🪑 Get hall seats          | Public  |
| POST         | /api/halls/{id}/seats            | ➕ Create seat             | Private |
| PUT          | /api/halls/seats/{id}            | ✏️ Update seat             | Private |
| DELETE       | /api/halls/seats/{id}            | ❌ Delete seat             | Private |
## 🎟️ Showtimes
| Request Type | URL                              | Functionality                  | Access  |
|--------------|----------------------------------|--------------------------------|---------|
| GET          | /api/showtimes                   | 📃 Get all showtimes           | Public  |
| GET          | /api/showtimes/{id}              | 🔍 Get showtime by ID          | Public  |
| GET          | /api/movies/{id}/showtimes       | 🎬 Get showtimes by movie      | Public  |
| POST         | /api/showtimes                   | ➕ Create showtime             | Private |
| PUT          | /api/showtimes/{id}              | ✏️ Update showtime             | Private |
| DELETE       | /api/showtimes/{id}              | ❌ Delete showtime             | Private |
  

## 💳 Bookings (Seats)
| Request Type | URL                               | Functionality                          | Access  |
|--------------|-----------------------------------|----------------------------------------|---------|
| POST         | /api/bookings/reserve             | 🪑 Reserve selected seats (5 min hold) | Private |
| POST         | /api/bookings/{bookingId}/confirm | ✅ Simulate pay + confirm → BOOKED     | Private |
| GET          | /api/bookings                     | 📃 List my bookings                    | Private |
| GET          | /api/bookings/{bookingId}         | 🔍 Get my booking                      | Private |
| GET          | /api/bookings/pending             | ⏳ Get pending booking for showtime    | Private |
| POST         | /api/bookings/{bookingId}/cancel  | ↩️ Cancel hold (release seats)         | Private |

## 🛠️ Admin
| Request Type | URL                      | Functionality     | Access  |
|--------------|--------------------------|-------------------|---------|
| GET          | /api/admin/users         | 📃 List users     | Private |
| GET          | /api/admin/users/{id}    | 🔍 Get user       | Private |
| PATCH        | /api/admin/users/{id}    | ✏️ Update user    | Private |
| GET          | /api/admin/bookings      | 📃 List bookings  | Private |
| GET          | /api/admin/bookings/{id} | 🔍 Get booking    | Private |
| GET          | /api/users/me            | 👤 Me profile     | Private |
| PATCH        | /api/users/me            | ✏️ Update profile | Private |

 
# 💠 ERD (temp)
___
![https://i.imgur.com/NgEwNxW.png](https://i.imgur.com/NgEwNxW.png)

# 🗓️ Trello
___
https://trello.com/b/ZRSCRzBm/project-3-jdb-cinemall


## Backend (Java "Maven" see `pom.xml`)
___
```bash
Open the app in IntelliJ and run, api will be active on:
http://localhost:8080
```

- **Java 17**, **Spring Boot** (parent BOM in `pom.xml`)
- **Spring Web MVC**, **Security**, **Data JPA**, **Validation**, **Mail**
- **PostgreSQL** driver, **Lombok**, **DevTools**
- **jjwt** `0.12.x` (API + impl + Jackson)

## Frontend (Vite + React + TS):
___
```bash
cd cinemall-web  
npm install  
npm run dev  
http://localhost:5173/
```


**Runtime**

- `react`, `react-dom`, `react-router-dom`
- `radix-ui`, `class-variance-authority`, `clsx`, `tailwind-merge`

**Build / UI tooling**

- `vite`, `@vitejs/plugin-react`, `typescript`, `tailwindcss`, `@tailwindcss/vite`
- `shadcn` (CLI + `shadcn/tailwind.css`), `tw-animate-css`, `@fontsource-variable/geist`
- `eslint`, `typescript-eslint`, `eslint-plugin-react-hooks`, `eslint-plugin-react-refresh`, `globals`

**Node:** `>=20.19.0` (see `package.json`).


## Demo users (seeded when `users` table is empty)
___
Seeded in **`DataSeeder.java`** only if the **`users`** table has no rows.

| Email | Password | Role       |
|-------|----------|------------|
| `admin@cinemall.local` | `admin123` | **ADMIN**  |
| `demo@cinemall.local` | `demo123` | **USER**   |


## Maintenance & Upgrades
___
```bash
cd cinemall-web

# Security audit
npm audit
npm audit fix

# See outdated packages (package.json)
npm outdated

# Update/Upgrade packages.
npm update
npm upgrade

# Build
npm run build

# Run Dev
npm run dev
```


## Docs & References links
___
| Tool                         | URL                                                  |
|------------------------------|------------------------------------------------------|
| **shadcn/ui - Vite install** | https://ui.shadcn.com/docs/installation/vite         |
| **shadcn/ui - home**         | https://ui.shadcn.com/                               |
| **Tailwind CSS v4 + Vite**   | https://tailwindcss.com/docs/installation/using-vite |
| **Vite**                     | https://vite.dev/guide/                              |
| **React**                    | https://react.dev/                                   |
| **React Router**             | https://reactrouter.com/                             |
| **Spring Boot**              | https://spring.io/projects/spring-boot               |
| **Spring Boot Initializer**  | https://start.spring.io         |
| **jjwt (0.12.x)**            | https://github.com/jwtk/jjwt                         |