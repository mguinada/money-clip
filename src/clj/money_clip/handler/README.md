# `money-clip.handler`

Handlers map [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) routes to actions. The routes are declared at [Duct](https://github.com/duct-framework/duct)'s [configuration descriptor](/resources/money_clip/config.edn).

## Handlers

| Handler      | Description                                  |
| ------------ | -------------------------------------------- |
| user         | Maps routes to user bound operations         |
| bank-account | Maps routes to bank account bound operations |