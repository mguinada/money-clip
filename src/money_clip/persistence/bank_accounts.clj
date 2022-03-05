(ns money-clip.persistence.bank-accounts
  (:require [clojure.spec.alpha :as s]
            [duct.database.sql]
            [clojure.java.jdbc :as jdbc]
            [money-clip.persistence :as p]
            [money-clip.model.user :as u]
            [money-clip.persistence.users :as users]
            [money-clip.model.bank-account :as ba]))

(def ^:private serialize
  (p/serializer ba/bank-account :id :user_id :name :bank_name :created_at :updated_at))

(defn serializer
  [row]
  (if-not (nil? row)
    (let [user (apply u/user (vals (select-keys row [:user_id :user_email :user_first_name :user_last_name :user_active :user_created_at :user_updated_at])))]
      (ba/bank-account (:id row) user (:name row) (:bank_name row) (:created_at row) (:updated_at row)))
    nil))

(defprotocol BankAccounts
  (create-bank-account [db bank-account])
  (find-bank-account-by-id [db id])
  (find-bank-accounts-by-user [db user]))

(defn- entities-to-ids
  [{user ::ba/user :as bank-account}]
  (-> bank-account
      (dissoc ::ba/user)
      (assoc ::ba/user-id (::u/id user))))

(defn- ids-to-entities
  [db {user-id ::ba/user :as bank-account}]
  (-> bank-account
      (assoc ::ba/user (-> (users/find-user-by-id db user-id) (dissoc ::u/password)))))

(extend-protocol BankAccounts
  duct.database.sql.Boundary
  (create-bank-account [{db :spec :as db-spec} bank-account]
    (p/check-spec! ::ba/bank-account bank-account)
    (let [results (jdbc/insert! db :bank_accounts (-> bank-account entities-to-ids p/underscore-keys))]
      (ids-to-entities db-spec (-> results serialize))))
  (find-bank-account-by-id [{db :spec} id]
    (let [results (jdbc/query db ["SELECT ba.id, ba.user_id, ba.name, ba.bank_name, ba.created_at, ba.updated_at, u.email AS user_email, u.first_name AS user_first_name, u.last_name AS user_last_name, u.active AS user_active, u.created_at AS user_created_at, u.updated_at AS user_updated_at FROM bank_accounts AS ba JOIN users as u on u.id = ba.user_id WHERE ba.id = ?", id])]
      (-> results first serializer)))
  (find-bank-accounts-by-user [{db :spec} {user-id ::u/id}]
    (let [results (jdbc/query db ["SELECT ba.id, ba.user_id, ba.name, ba.bank_name, ba.created_at, ba.updated_at, u.email AS user_email, u.first_name AS user_first_name, u.last_name AS user_last_name, u.active AS user_active, u.created_at AS user_created_at, u.updated_at AS user_updated_at FROM bank_accounts AS ba JOIN users as u on u.id = ba.user_id WHERE ba.user_id = ?", user-id])]
        (map serializer results))))

(s/fdef create-bank-account
  :args (s/cat
         :db ::p/db
         :bank-account ::ba/bank-account)
  :ret ::ba/bank-account)

(s/fdef find-bank-account-by-id
  :args (s/cat
         :db ::p/db
         :id nat-int?)
  :ret (s/or :nil nil? :bank-account ::ba/bank-account))


(s/fdef find-bank-accounts-by-user
  :args (s/cat
         :db ::p/db
         :user ::u/user)
  :ret (s/coll-of ::ba/bank-account))
