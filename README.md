# `Bantamweight`

This is an `http4k` and `Svelte` starter template, which is still under development. "Bantamweight" because it is a lighter stack but still packs a punch.

To get started, you should have [JDK 11](https://openjdk.java.net/projects/jdk/11/), [nodejs](https://nodejs.org/en/) (at least v14) and [pnpm](https://pnpm.io/installation). If you don't want to use in-memory sqlite, install [sqlite](https://www.sqlite.org/download.html) if needed and create a `bantamweight.db`.

## Features

This app is still under development but it already includes a good part of the front and backend code for JWT auth, including token refresh with an httpOnly cooke, log-in and -out, password change, admin user management. On the auth side, the user signup and password reset flow is missing. Further features will be added to demonstrate other ways to do things with `http4k` and `Svelte`.

## Backend

The app will use `application-dev.yaml` by default if a file path to `application.yaml` is not added to the `ConfigDelegate.Kt` file. You must also set, at a minimum, the `BANTAMWEIGHT_DB_FILEPATH`. There are a number of ways to override the yaml values. Use env variables such as `APP__ENV` or for another methods see [hoplite](https://github.com/sksamuel/hoplite). See `Parameters` below for list of every possible flag.

...start the backend

```
  ./gradlew runShadow
```

The app will be available at [localhost:9000](http://localhost:9000)

New dependencies should be added to `versions.properties`, see [refreshVersions](https://github.com/jmfayard/refreshVersions)

To make the app even lighter-weight, I have used `sqlite` with [sqldelight](https://github.com/cashapp/sqldelight). While sqldelight is easy to get started with and gives you typed queries. The migrations happen programmatically. It does not support all of sqlites's latest features. A more heavyweight ORM solution would be e.g. [jooq](https://github.com/jOOQ/jOOQ) plus [flyway](https://flywaydb.org/) for migrations, which also supports other dbs like postgres well.

### Command-line flags

| Flag                         | Meaning                                      | Default Value              |
| ---------------------------- | -------------------------------------------- | -------------------------- |
| `APP__ENV`                   | Set `"DEV"` or `"PROD"` for backend config   | `"DEV"`                    |
| `APP__PORT`                  | Server port in development                   | `9000`                     |
| `DATABASE__FILE_PATH`        | Path to the sqlite db file                   | `"/data/bantamweight.db"`  |
| `DATABASE__USER__LAST_NAME`  | Seed user last name                          | `Super`                    |
| `DATABASE__USER__FIRST_NAME` | Seed user first name                         | `User`                     |
| `DATABASE__USER__EMAIL`      | Seed user email                              | `bantamweight@mail.com`    |
| `DATABASE__USER__PASSWORD`   | Seed user password                           | `password`                 |
| `DATABASE__USER__ROLES`      | Seed user roles                              | [`admin`]                  |
| `AUTH__SECRET`               | Long random string for encrypting JWT tokens | `changeme-[randomletters]` |
| `AUTH__ROLES`                | User roles                                   | [`admin`, `user`]          |

### Docker build args

You can adjust behavior of the Docker container by passing these parameters with `docker run -e`:

| Environment Variable             | Meaning                                                                           |
| -------------------------------- | --------------------------------------------------------------------------------- |
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to the Google storage credentials needed for storing replicas                |
| `REPLICA_URL`                    | The address for Litestream to find the correct storage, e.g. `gcs://bantamweight` |

## Frontend

...to run in development

```
  cd frontend && pnpm install && pnpm run dev
```

This will proxy requests from the frontend through a development server. If you build the app with `pnpm run build`, you can see the app served from [localhost:9000](http://localhost:9000)

This [Svelte](https://svelte.dev/) + [Vite](https://vitejs.dev/config/#config-file-resolving) app is very light-weight in terms of bundle size. The dependencies have been kept to a minimum. The build also creates an `public/assets-manifest.js` file with a list of the static assets which can be used to precache resources in a service worker. I plan to add a service-worker starter in the future. [SvelteKit](https://kit.svelte.dev/) is not used here because the story for SPA's at this point is fairly poor--it is more focused on server-rendered apps. This may change in the future.

If you feel the need for a Svelte component library, [Carbon components Svelte](https://github.com/IBM/carbon-components-svelte) is fairly large and has typescript definitions.

## Deployment

Using the Dockerfile in the project root, the app can be run in a container and persist the sqlite database via [Litestream](https://litestream.io/) at low-cost and without the need for a volume. Use any cloud providers that run docker images that you like, [fly.io](https://fly.io/) or [Heroku](https://www.heroku.com/) are good choices (this app is deployed currently to Heroku). Litestream will replicate the database to a storage bucket like Google Cloud Storage. When the server is rebuilt, the database will be restored in the container. While this is easy and cheap, it is probably not what you want for highly-available apps where data-integrity and security is the first concern. In this case, you may want to consider a database outside of the container.
