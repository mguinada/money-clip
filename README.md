# money-clip

FIXME: description



[TOC]

## Development environment setup

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

### Booting the development environment

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

## Legal

Copyright © 2022 Miguel Guinada
