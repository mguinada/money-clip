(ns money-clip.handler.restful.resources
  (:require [money-clip.handler.restful.rest :refer [defresource]]))

(defresource user
  :exclude [:password :active]
  :links {:self "/api/user" :bank-accounts "/api/bank-accounts" :change-password "/api/user/change-password"})

(defresource bank-account
  :exclude [:user]
  :links {:self "/api/bank-accounts/{id}" :user "/api/user"})
