(ns money-clip.handler.restful.resources
  (:require [money-clip.handler.restful.rest :refer [defresource]]))

(defresource user
  :exclude [:password :active]
  :links {:bank-accounts "/bank-accounts"})

(defresource bank-account
  :include {:user_id [:user :id]}
  :exclude [:user :user_id]
  :links {:self "/bank-accounts/{id}"})
