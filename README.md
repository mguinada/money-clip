# money-clip

`money-clip` is a personal expense tracker 

## Documentation

[API](https://mguinada.stoplight.io/docs/money-clip/branches/main/e9aa249dc9030-login)

## Application layers

:information_source: _links point to each layer's README file_

| Component         | Description |
| ----------------- | ----------- |
| [Request handling](/src/money_clip/handler/README.md) | Maps HTTP [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) URLs (routes) to actions |
| [RESTful resource models](/src/money_clip/handler/restful/README.md) |Maps domain entities to their [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) representation|
| [Middleware](/src/money_clip/duct/README.md) | Pluggable infrastructure-bound actions |
| [Domain model](/src/money_clip/model/README.md) | The business domain entities which encapsulates the business rules |
| [Persistence](/src/money_clip/persistence/README.md) | Persists the domain entities to the database |


## Technological stack

Core technologies employed in the making of this project

| Technology     | Homepage |
| ---------------| -------- |
| Database       | [PostgreSQL 1.5.0](https://www.postgresql.org/)   |
| Framework      | [Duct](https://github.com/duct-framework/duct) |
| Authentication | [Buddy](https://github.com/funcool/buddy-core) |
| Runtime        | [Clojure](https://clojure.org/) |

## Development

When you first clone this repository, `cd` into the project and run:

```sh
lein duct setup
```

This will create files for local configuration, and prepare your system for the project.

At `dev/resources/local.edn` add your RDBMS username and password.

:warning: this file should never be uploaded at your version control system.

```edn
{:duct.database/sql
 {:username "THE RDBMS USERNAME"
  :password "THE RDBMS PASSWORD"}}
```

Create the development and test databases at postgres RDBMS:

```sql
CREATE DATABASE money_clip_development;
CREATE DATABASE money_clip_test;
```

Add an environment variable with a session secret key

:information_source: See the `Creating a signed token` section at [Securing Clojure Microservices using buddy - Part 1: Creating Auth Tokens](https://rundis.github.io/blog/2015/buddy_auth_part1.html)

```bash
export JWT_SECRET=<secret>
```

### Booting the backend

Start with a REPL.

```sh
lein repl
```

Load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to boot the app

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Running the automated tests

```clojure
user=> (dev)
:loaded
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

### Booting the front end

```sh
npx shadow-cljs watch app
```

## Legal

Copyright © 2022 Miguel Guinada
