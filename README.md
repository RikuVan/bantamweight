# `Bantamweight`

This is an `http4k` and `Svelte` starter template, which is still under development. "Bantamweight" because it is a lighter stack but still packs a punch.

To get started, you should have [JDK 11](https://openjdk.java.net/projects/jdk/11/), [nodejs](https://nodejs.org/en/) (at least v14) and [pnpm](https://pnpm.io/installation). If you don't want to use in-memory sqlite, install [sqlite](https://www.sqlite.org/download.html) if needed and create a `bantamweight.db`.

## Features

This app is still under development but it already includes a good part of the front and backend code for JWT auth, including token refresh with an httpOnly cooke, log-in and -out, password change, admin user management. On the auth side, the user signup and password reset flow is missing. Further features will be added to demonstrate other ways to do things with `http4k` and `Svelte`.

## Backend

Use the `application-dev-TEMPLATE.yaml` to create your own `application-dev.yaml`, providing environment for development.

...start the backend

```
  ./gradlew shadowJar
```

The app will be available at [localhost:9000](http://localhost:9000)

New dependencies should be added to `versions.properties`, see [refreshVersions](https://github.com/jmfayard/refreshVersions)

To make the app even lighter-weight, I have used `sqlite` with [sqldelight](https://github.com/cashapp/sqldelight). While sqldelight is easy to get started with and gives you typed queries, the migrations happen programmatically and it does not support all of sqlites's latest features. A more heavyweight ORM solution would be e.g. [jooq](https://github.com/jOOQ/jOOQ) plus [flyway](https://flywaydb.org/) for migrations, which also supports other dbs like postgres well.

## Frontend

...to run in development

```
  cd frontend && pnpm run install && pnpm run dev
```

This will proxy requests from the frontend through a development server. If you build the app with `pnpm run build`, you can see the app served from [localhost:9000](http://localhost:9000)

This [Svelte](https://svelte.dev/) + [Vite](https://vitejs.dev/config/#config-file-resolving) app is very light-weight in terms of bundle size. The dependencies have been kept to a minimum. The build also creates an `public/assets-manifest.js` file with a list of the static assets which can be used to precache resources in a service worker. I plan to add a service-worker starter in the future. [SvelteKit](https://kit.svelte.dev/) is not used here because the story for SPA's at this point is fairly poor--it is more focused on server-rendered apps. This may change in the future.

If you feel the need for a Svelte component library, [Carbon components Svelte](https://github.com/IBM/carbon-components-svelte) is fairly large and has typescript definitions.

## Deployment

TODO: digital ocean maybe?
