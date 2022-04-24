(ns money-clip.middleware
  "Application specifc middleware"
  (:require [integrant.core :as ig]
            [buddy.auth :as auth]))

(defn- authenticate!
  [request]
  (when-not (auth/authenticated? request) (auth/throw-unauthorized)))

(defn- wrap-authorization
  "Checks if the user is authenticated. If's it's not an HTTP status 401 Forbidden is issued.
   It is meant to be used together with https://github.com/duct-framework/middleware.buddy."
  [handler]
  (fn 
    ([request]
     (authenticate! request)
     (handler request))
    ([request respond raise]
     (authenticate! request)
     (handler request respond raise))))

(defmethod ig/init-key ::authorize [_ _]
 (fn [handler]
   (wrap-authorization handler)))
