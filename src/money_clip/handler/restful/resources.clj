(ns money-clip.handler.restful.resources
  (:require [money-clip.handler.restful.rest :refer [defresource]]))

(defresource user
  :exclude [:password :active]
  :links {:self "/user" :bank-accounts "/bank-accounts"})

(defresource bank-account
  :exclude [:user]
  :links {:self "/bank-accounts/{id}" :user "/user"})
