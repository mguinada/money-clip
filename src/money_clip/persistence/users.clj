(ns money-clip.persistence.users
  (:require [clojure.spec.alpha :as s]
            [duct.database.sql]
            [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hs]
            [money-clip.persistence :as p]
            [money-clip.utils :as ut]
            [money-clip.model.user :as u]))

(def underscore-keys (partial ut/map-keys ut/underscore))

(defn check-spec!
  "Checks if map `m` conforms to spec `s`."
  [s m]
  (if (= ::s/invalid (s/conform s m))
    (throw (ex-info "Invalid entity"
                    {:type ::spec-violation
                     :spec s
                     :cause (s/explain-str s m)
                     :explain (s/explain-data s m)
                     :invalid-data m}))
    true))

(def ^:private serialize
  (p/serializer u/user :id :email :password :first_name :last_name :active :created_at :updated_at))

(defprotocol Users
  (create-user [db user])
  (find-user-by-email [db email])
  (authenticate-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec :as this} user]
    (check-spec! ::u/user user)
    (if-not (find-user-by-email this (::u/email user))
      (let [user (update user ::u/password #(hs/derive % {:alg :pbkdf2+sha512}))
            results (jdbc/insert! db :users (underscore-keys user))]
        (-> results serialize))
      (throw (ex-info "Email already taken" {:reason ::email-taken :email (::u/email user)}))))
  (find-user-by-email [{db :spec} email]
    (let [results (jdbc/query db ["SELECT id, email, password, first_name, last_name, active, created_at, updated_at FROM users WHERE LOWER(email) = LOWER(?)", email])]
      (-> results serialize)))
  (authenticate-user [db email password]
    (if-let [user (find-user-by-email db email)]
      (if (u/active? user) (u/authenticate user password) nil)
      nil)))

(s/fdef create-user
  :args (s/cat
         :db ::p/db
         :user ::u/user)
  :ret ::u/user)

(s/fdef find-user-by-email
  :args (s/cat
         :db ::p/db
         :email ::u/email)
  :ret (s/or :nil nil? :user ::u/user))

(s/fdef authenticate-user
  :args (s/cat
         :db ::p/db
         :email ::u/email
         :password string?)
  :ret (s/or :nil nil? :user ::u/user))
