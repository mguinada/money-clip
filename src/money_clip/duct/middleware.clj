(ns money-clip.duct.middleware
  "Application specifc middleware"
  (:require [integrant.core :as ig]
            [buddy.auth :as auth]
            [money-clip.errors :as e]))

(defn- authenticate!
  [request]
  (when-not (auth/authenticated? request) (auth/throw-unauthorized e/unautorized)))

(defn- wrap-authorization
  "Checks if the user is authenticated. If's it's not an HTTP status 401 Forbidden is issued.
   It is meant to be used together with https://github.com/duct-framework/middleware.buddy."
  [handler]
  (fn
    ([request]
     (authenticate! request)
     (handler request))
    ([request response raise]
     (authenticate! request)
     (handler request response raise))))

(defn- wrap-in-try-catch
  "Error handling middleware.
   Wraps the handler in a try/catch block. If an error is raise and is
   cataloged as `respondable` and JSON response will be server, otherwise an exception will
   be raised."
  [handler]
  (fn
    ([request]
     (e/try-catch (handler request)))
    ([request response raise]
     (e/try-catch (handler request response raise)))))

(defmethod ig/init-key ::authorize
  [_ _]
  (fn [handler]
    (wrap-authorization handler)))

(defmethod ig/init-key ::error-handler
  [_ _]
  (fn [handler]
    (wrap-in-try-catch handler)))

(defmethod ig/init-key ::unauthorized-handler
  [_ _]
  (fn
    [request error-data]
    (if (auth/authenticated? request)
      {:status 403 :headers {} :body e/permission-denied}
      {:status 401 :headers {} :body error-data})))
