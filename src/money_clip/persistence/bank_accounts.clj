(ns money-clip.persistence.bank-accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [duct.database.sql]
            [clojure.java.jdbc :as jdbc]
            [money-clip.errors :as e]
            [money-clip.persistence.sql :as sql]
            [money-clip.persistence :as p]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]))

(defn serializer
  [row]
  (if-not (nil? row)
    (let [user (apply u/user (vals (select-keys row [:user_id :user_email :user_first_name :user_last_name :user_active :user_created_at :user_updated_at])))]
      (ba/bank-account (:id row) user (:name row) (:bank_name row) (:created_at row) (:updated_at row)))
    nil))

(defprotocol BankAccounts
  (create-bank-account [db bank-account])
  (find-bank-account-by-id [db id])
  (find-bank-accounts-by-user [db user])
  (find-bank-account-by-user-and-id [db user id])
  (find-bank-account-by-user-and-name [db user name])
  (update-bank-account [db user bank-account]))

(defn- entities-to-ids
  [{user ::ba/user :as bank-account}]
  (-> bank-account
      (dissoc ::ba/user)
      (assoc ::ba/user-id (::u/id user))))

(defn- validate-name-uniqueness!
  ([db-spec user name]
   (validate-name-uniqueness! db-spec user name nil))
  ([db-spec user name id]
   (let [ba (find-bank-account-by-user-and-name db-spec user name)]
     (cond
       (nil? ba) true
       (and (nil? ba) (nil? id)) true
       (and (= id (::ba/id ba)) (not (nil? ba))) true
       :else (throw (e/uniqueness-violation-error
                     (str "A bank account named `" name "` already exists")
                     ::bank-account-name-taken {:attribute :name :value name}))))))

(extend-protocol BankAccounts
  duct.database.sql.Boundary
  (create-bank-account [{db :spec :as db-spec} {user ::ba/user bank-account-name ::ba/name :as bank-account}]
    (p/check-spec! ::ba/bank-account bank-account)
    (validate-name-uniqueness! db-spec user bank-account-name)
    (let [results (jdbc/insert! db :bank_accounts (-> bank-account entities-to-ids p/underscore-keys))]
      (find-bank-account-by-id db-spec (-> results first :id))))
  (find-bank-account-by-id [{db :spec} id]
    (let [results (jdbc/query db (-> sql/select-bank-accounts (sql/where [:= :bank_accounts.id id]) sql/format))]
      (-> results first serializer)))
  (find-bank-accounts-by-user [{db :spec} {user-id ::u/id}]
    (let [results (jdbc/query db (-> sql/select-bank-accounts (sql/where [:= :bank_accounts/user_id user-id]) sql/format))]
      (map serializer results)))
  (find-bank-account-by-user-and-id [{db :spec} {user-id ::u/id} id]
    (let [results (jdbc/query db (-> sql/select-bank-accounts (sql/where [:= :bank_accounts/user_id user-id] [:= :bank_accounts.id id]) sql/format))]
      (-> results first serializer)))
  (find-bank-account-by-user-and-name [{db :spec} {user-id ::u/id} name]
    (let [results (jdbc/query db (-> sql/select-bank-accounts (sql/where [:= :bank_accounts/user_id user-id] [:= :%LOWER.name (str/lower-case name)]) sql/format))]
      (-> results first serializer)))
  (update-bank-account [{db :spec :as db-spec} {user-id ::u/id :as user} {id ::ba/id name ::ba/name bank-name ::ba/bank-name :as bank-account}]
    (do
      (p/check-spec! ::ba/bank-account bank-account)
      (validate-name-uniqueness! db-spec user name id)
      (jdbc/execute! db (sql/update-bank-account id user-id name bank-name (p/timestamp)))
      (find-bank-account-by-user-and-id db-spec user id))))

(s/fdef create-bank-accoun
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

(s/fdef find-bank-account-by-user-and-name
  :args (s/cat
         :db ::p/db
         :user ::u/user
         :name string?)
  :ret (s/or :bank_acount ::ba/bank-account :nil nil?))

(s/fdef find-bank-account-by-user-and-id
  :args (s/cat
         :db ::p/db
         :user ::u/user
         :id nat-int?)
  :ret (s/or :bank_acount ::ba/bank-account :nil nil?))

(s/fdef update-bank-account
  :args (s/cat
         :db ::p/db
         :user ::u/user
         :bank_acount ::ba/bank-account)
  :ret (s/or :bank_acount ::ba/bank-account :nil nil?))
