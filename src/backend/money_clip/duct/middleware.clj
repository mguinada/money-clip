(ns money-clip.duct.middleware
  "Application specifc middleware"
  (:require [integrant.core :as ig]
            [buddy.auth :as auth]
            [money-clip.errors :as e]
            [money-clip.model.user :as u]
            [money-clip.persistence.users :as users]
            [money-clip.utils :as ut]))

(defn- authenticate!
  "Authenticates the user  the performed the request.

   If the user is authenticated and succesfuly retrived from the database, it will be added to the request,
   otherwise an unauthorized exception will be throw which will in turn serve a 401 Unauthorized response."
  [{{user-id ::u/id} :identity :as request} db]
  (when-not (auth/authenticated? request) (auth/throw-unauthorized e/unauthorized))
  (if-let [user (-> (users/find-user-by-id db user-id) (dissoc ::u/password))]
    (assoc request :user user)
    (auth/throw-unauthorized e/unauthorized)))

(defn- wrap-authorization
  "Checks if the user is authenticated. If's it's not an HTTP status 401 Forbidden is issued.
   It is meant to be used together with https://github.com/duct-framework/middleware.buddy."
  [handler db]
  (fn
    ([request]
     (handler (authenticate! request db)))
    ([request response raise]
     (handler (authenticate! request db) response raise))))

(defn- wrap-in-try-catch
  "Error handling middleware.
   Wraps the handler in a try/catch block. If an error is raised and is
   cataloged as `respondable` and JSON response will be server, otherwise an exception will
   be raised."
  [handler]
  (fn
    ([request]
     (e/try-catch (handler request)))
    ([request response raise]
     (e/try-catch (handler request response raise)))))

(defn- wrap-in-dasherize
  "Converts JSON based snake case keys to dasherized keys"
  [handler]
  (fn
    ([request]
     (handler (assoc request :body-params (ut/dasherize-keys (:body-params request)))))
    ([request response raise]
     (handler (assoc request :body-params (ut/dasherize-keys (:body-params request))) response raise))))

(defmethod ig/init-key ::authorize
  [_ {:keys [db]}]
  (fn [handler]
    (wrap-authorization handler db)))

(defmethod ig/init-key ::error-handler
  [_ _]
  (fn [handler]
    (wrap-in-try-catch handler)))

(defmethod ig/init-key ::unauthorized-handler
  [_ _]
  (fn
    [request error-data]
    (if (auth/authenticated? request)
      {:status 403 :headers {} :body e/access-denied}
      {:status 401 :headers {} :body error-data})))

(defmethod ig/init-key ::dasherize
  [_ _]
  (fn [handler]
    (wrap-in-dasherize handler)))
