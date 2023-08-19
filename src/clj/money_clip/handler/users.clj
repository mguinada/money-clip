(ns money-clip.handler.users
  "User request handlers"
  (:require [ataraxy.response :as response]
            [ring.util.http-response]
            [integrant.core :as ig]
            [buddy.sign.jwt :as jwt]
            [tick.core :as t]
            [money-clip.persistence.users :as users]
            [money-clip.model.user :as u]
            [money-clip.errors :as e]
            [money-clip.handler.restful.resources :as r]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password password-confirmation first-name last-name] :ataraxy/result}]
    (let [user (users/create-user db (u/new-user email password first-name last-name) password-confirmation)]
      [::response/created (str "/users/" (::u/id user)) (r/user-resource user)])))

(defmethod ig/init-key ::login [_ {:keys [db jwt-secret]}]
  (letfn [(sign-token [user]
            (if-not (nil? user)
              (->>
               {:exp (t/>> (t/now) (t/new-period 1 :days))}
               (jwt/sign (select-keys user [::u/id ::u/email]) jwt-secret)
               (assoc user ::u/auth-token))
              nil))]
    (fn [{[_ email password session] :ataraxy/result}]
      (if-let [user (-> (users/authenticate-user db email password) sign-token)]
        (-> (r/user-resource (sign-token user))
            ring.util.http-response/ok
            (assoc :session (assoc session :token (::u/auth-token user))))
        [::response/unauthorized e/unauthorized]))))

(defmethod ig/init-key ::user [_ _]
  (fn [{user :user}]
    [::response/ok (r/user-resource user)]))

(defmethod ig/init-key ::update [_ {:keys [db]}]
  (fn [{user :user [_ first-name last-name] :ataraxy/result}]
    [::response/ok (r/user-resource (users/update-user db (assoc user ::u/first-name first-name ::u/last-name last-name)))]))

(defmethod ig/init-key ::change-password [_ {:keys [db]}]
  (fn [{user :user [_ current-password password password-confirmation] :ataraxy/result}]
    [::response/ok (r/user-resource (users/update-user-password db user current-password password password-confirmation))]))

(defmethod ig/init-key ::get-session [_ _]
  (fn [{[_ {token :token}] :ataraxy/result}]
    (if (some? token)
      [::response/ok {:token token}]
      [::response/not-found])))

(defmethod ig/init-key ::delete-session [_ _]
  (fn [{[_ _] :ataraxy/result}]
    (-> (ring.util.http-response/ok)
        (assoc :session nil))))
