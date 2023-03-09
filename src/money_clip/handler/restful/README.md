# `money-clip.handler.restful`

[RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) resources for each of the publisher domain model entities

## Resources

| Resource    | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| User        | [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) representation of the application user |
| BankAccount | [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) representation of a user's bank account |



## The `defresource`macro

The `defresource` macro allows to declaratively define a [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) for a model entity. It supports attribute inclusion for projection of nested map elements, attribute exclusion, attribute ordering and [HATEAOS](https://en.wikipedia.org/wiki/HATEOAS).

Let's look at an example considering the following user model.

```clojure
dev=> (require '[money-clip.model.user :as u])
dev=> (def user {::u/id 123
                 ::u/active true
                 ::u/email "mguinada+example@example.com"
                 ::u/first-name "Miguel"
                 ::u/last-name "Guinada"
                 ::u/password "pa66w0rd"
                 ::u/social #:money-clip.model.user {:github "@mguinada" :twitter "@mguinada"}})
```

Now, let's define the user's resource

```clojure
dev=> (require '[money-clip.handler.restful.rest :refer [defresource]])

dev=> (defresource user
        :include {:twitter [:social :twitter] :github [:social :github]}
        :exclude [:password :active :social]
        :attr-order [:id :email :first-name :last-name :github :twitter :_links]
        :links {:self "/user" :bank-accounts "/user/{id}/bank-accounts"})	
#'dev/user-resource
```

The macro creates a function named `user-resource`.

```clojure
dev=> (user-resource user)
{:user {:id 123
        :email "mguinada+example@example.com"
        :first_name "Miguel"
        :last_name "Guinada"
        :github "@mguinada"         
        :twitter "@mguinada"
        :_links {:self "/user" :bank_accounts "/user/123/bank-accounts"}}}
```

Some of the properties of the resource, as declared with the `defresource` macro, are:

* The keys `:money-clip.model.user/password`, `:money-clip.model.user/active` and `:money-clip.model.user/social` were excluded from the resource
* The nested keys `:money-clip.model.user/github` and `:money-clip.model.user/twitter` were respectively projected to the keys `:github` and `:twitter`
* The key order was as declared by the macro's `:attr-order` argument
* The [HATEAOS](https://en.wikipedia.org/wiki/HATEOAS) link relations `:bank_accounts` and `:self` where generated as specified
* The qualified keys were converted to unqualified keys
