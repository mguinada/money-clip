(ns money-clip.persistence.users
  (:require [clojure.spec.alpha :as s]
            [duct.database.sql]
            [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hs]
            [money-clip.persistence :as p]
            [money-clip.model.user :as u]))

(def ^:private serialize
  (p/serializer u/user :id :email :password :first_name :last_name :active :created_at :updated_at))

(defprotocol Users
  (create-user [db user password-confirmation])
  (find-user-by-id [db user-id])
  (find-user-by-email [db email])
  (authenticate-user [db email password]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec :as this} user password-confirmation]
    (p/check-spec! ::u/user user)
    (if (= (::u/password user) password-confirmation)
      (if-not (find-user-by-email this (::u/email user))
        (let [user (update user ::u/password #(hs/derive % {:alg :pbkdf2+sha512}))
              results (jdbc/insert! db :users (p/underscore-keys user))]
          (-> results serialize))
        (throw (ex-info "Email already taken" {:reason ::email-taken :email (::u/email user)})))
      (throw (ex-info "Passwords don't match" {:reason ::passwords-dont-match :email (::u/email user)}))))
  (find-user-by-id [{db :spec} user-id]
    (let [results (jdbc/query db ["SELECT id, email, password, first_name, last_name, active, created_at, updated_at FROM users WHERE id = ?", user-id])]
      (-> results serialize)))
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
         :user ::u/user
         :password-confirmation string?)
  :ret ::u/user)

(s/fdef find-user-by-id
  :args (s/cat
         :db ::p/db
         :id nat-int?)
  :ret (s/or :nil nil? :user ::u/user))

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
