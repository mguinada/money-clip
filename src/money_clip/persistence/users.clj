(ns money-clip.persistence.users
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [duct.database.sql]
            [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hs]
            [money-clip.errors :as e]
            [money-clip.persistence.sql :as sql]
            [money-clip.persistence :as p]
            [money-clip.model.user :as u]))

(def ^:private serialize
  (p/serializer u/user :id :email :password :first_name :last_name :active :created_at :updated_at))

(defprotocol Users
  (create-user [db user password-confirmation])
  (find-user-by-id [db id])
  (find-user-by-email [db email])
  (update-user [db user])
  (authenticate-user [db email password])
  (update-user-password [db user current-password password password-confirmation]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec :as db-spec} user password-confirmation]
    (p/check-spec! ::u/user user)
    (cond
      (not= (::u/password user) password-confirmation) (throw (e/passwords-dont-match-error "Passwords don't match" ::passwords-dont-match {:attribute :password}))
      (find-user-by-email db-spec (::u/email user)) (throw (e/uniqueness-violation-error "Email already taken" ::email-taken {:attribute :email :value (::u/email user)}))
      :else (let [user (update user ::u/password #(hs/derive % {:alg :pbkdf2+sha512}))
                  results (jdbc/insert! db :users (p/underscore-keys user))]
              (-> results serialize))))
  (update-user [{db :spec :as db-spec} {id ::u/id first-name ::u/first-name last-name ::u/last-name}]
    (do
      (jdbc/execute! db (sql/update-user id first-name last-name (p/timestamp)))
      (find-user-by-id db-spec id)))
  (find-user-by-id [{db :spec} id]
    (let [results (jdbc/query db (-> sql/select-users (sql/where := :id id) sql/format))]
      (-> results serialize)))
  (find-user-by-email [{db :spec} email]
    (let [results (jdbc/query db (-> sql/select-users (sql/where [:= :%LOWER.email (str/lower-case email)]) sql/format))]
      (-> results serialize)))
  (authenticate-user [db email password]
    (if-let [user (find-user-by-email db email)]
      (if (u/active? user) (u/authenticate user password) nil)
      nil))
  (update-user-password [{db :spec :as db-spec} {email ::u/email} current-password password password-confirmation]
    (if-let [{user-id ::u/id} (authenticate-user db-spec email current-password)]
      (do
        (when-not (= password password-confirmation) (throw (e/passwords-dont-match-error "Passwords don't match" ::passwords-dont-match {:attribute :password-confirmation})))
        (jdbc/execute! db (sql/update-user-password user-id (hs/derive password {:alg :pbkdf2+sha512}) (p/timestamp)))
        (find-user-by-id db-spec user-id))
      (throw (e/invalid-password "Authentication failed" ::invalid-password {:attribute :current-password})))))

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

(s/fdef update-user
  :args (s/cat
         :db ::p/db
         :user ::u/user)
  :ret ::u/user)

(s/fdef update-user-password
  :args (s/cat
         :db ::p/db
         :user ::u/user
         :current-password string?
         :password string?
         :password-confirmation string?)
  :ret ::u/user)
