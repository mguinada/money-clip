# money-clip

FIXME: description

## Developing

### Setup

Create the development and test databases at postgres RDBMS:

```sql
CREATE DATABASE money_clip_development;
CREATE DATABASE money_clip_test;
```

Add an environment var with a session secret key:

```bash
export JWT_SECRET=<secret>
```

When you first clone this repository, run:

```sh
lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

At `./dev/resources/local.edn` add your Postgresql username and password.
This sample assumes that your Postgresql password is stored at the environment variable `POSTGRES_PWD`.

```edn
{:duct.database/sql
 {:username "postgres"
  :password #duct/env "POSTGRES_PWD"}}
```

:warning: this file should never be uploaded at your version control system.

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

## Legal

Copyright © 2022 FIXME
