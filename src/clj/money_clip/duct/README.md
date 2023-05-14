# `money-clip.duct.middleware`

A collection of [duct](https://github.com/duct-framework) middleware written for this application. The middleware is declared at [Duct](https://github.com/duct-framework/duct)'s [configuration descriptor](/resources/money_clip/config.edn).



| Middleware                                 | Description                                                  |
| :----------------------------------------- | ------------------------------------------------------------ |
| `money-clip.duct.middleware/authorize`     | Checks if the user is authenticated. If's it's not an HTTP status 401 Forbidden is issued, otherwise the request will follow through and the requiring user will be added to the request. It is meant to be used together with the [Buddy middlware](https://github.com/duct-framework/middleware.buddy). |
| `money-clip.duct.middleware/dasherize`     | Converts JSON based snake case keys to dasherized keys.      |
| `money-clip.duct.middleware/error-handler` | Error handling middleware. Wraps the handler in a try/catch block. <br />If an error is raised and is cataloged as `respondable` and JSON response will be server, otherwise an exception will be raised. See `money-clip.errors` |