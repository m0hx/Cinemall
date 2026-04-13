# Cinemall

A modern cinema tickets booking system with real-time seat reservations, notifications, and secure payments.

## Quick start

| | URL / command |
|---|----------------|
| **API** | http://localhost:8080 |
| **Web UI** | http://localhost:5173 |


# 💠 ERD (temp)

![https://i.imgur.com/NgEwNxW.png](https://i.imgur.com/NgEwNxW.png)

# 🗓️ Trello

https://trello.com/b/ZRSCRzBm/project-3-jdb-cinemall


## Backend (Java "Maven" see `pom.xml`)

```bash
Open the app in IntelliJ and run, api will be active on:
http://localhost:8080
```

- **Java 17**, **Spring Boot** (parent BOM in `pom.xml`)
- **Spring Web MVC**, **Security**, **Data JPA**, **Validation**, **Mail**
- **PostgreSQL** driver, **Lombok**, **DevTools**
- **jjwt** `0.12.x` (API + impl + Jackson)

## Frontend (Vite + React + TS):

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

Seeded in **`DataSeeder.java`** only if the **`users`** table has no rows.

| Email | Password | Role       |
|-------|----------|------------|
| `admin@cinemall.local` | `admin123` | **ADMIN**  |
| `demo@cinemall.local` | `demo123` | **USER**   |


## Maintenance & Upgrades
```bash
cd cinemall-web

# Security audit
npm audit
npm audit fix

# See outdated packages (package.json)
npm outdated

# Apply safe range updates (patch/minor per package.json ^ and ~)
npm update
npm run build
```


## Docs & References links

| Tool                        | URL                                                  |
|-----------------------------|------------------------------------------------------|
| **shadcn/ui — Vite install** | https://ui.shadcn.com/docs/installation/vite         |
| **shadcn/ui — home**        | https://ui.shadcn.com/                               |
| **Tailwind CSS v4 + Vite**  | https://tailwindcss.com/docs/installation/using-vite |
| **Vite**                    | https://vite.dev/guide/                              |
| **React**                   | https://react.dev/                                   |
| **React Router**            | https://reactrouter.com/                             |
| **Spring Boot**             | https://spring.io/projects/spring-boot               |
| **Spring Boot Initializer** | https://start.spring.io         |
| **jjwt (0.12.x)**           | https://github.com/jwtk/jjwt                         |