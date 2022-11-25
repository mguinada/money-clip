(ns money-clip.persistence.sql
  (:require [clojure.core :as c]
            [honey.sql :as hsql]
            [honey.sql.helpers :refer [select select-distinct from
                                       join left-join right-join
                                       for group-by having union
                                       order-by limit offset values columns
                                       update insert-into set composite
                                       delete delete-from truncate] :as h])
  (:refer-clojure :exclude [filter for format group-by into partition-by set update]))

(def where h/where)

(defn format [st]
  (hsql/format st :namespace-as-table? true))

(def select-users
  (-> (select :id
              :email
              :password
              :first_name
              :last_name
              :active
              :created_at
              :updated_at)
      (from :users)))

(defn update-user
  [id first-name last-name updated-at]
  (-> (update :users)
      (set {:first_name first-name :last_name last-name :updated_at updated-at})
      (where [:= :id id])
      (format)))

(defn update-user-password
  [id password-hash updated-at]
  (-> (update :users)
      (set {:password password-hash :updated_at updated-at})
      (where [:= :id id])
      (format)))

(def select-bank-accounts
  (-> (select :bank_accounts/id
              :bank_accounts/user_id
              :bank_accounts/name
              :bank_accounts/bank_name
              :bank_accounts/created_at
              :bank_accounts/updated_at
              [:users/email :user_email]
              [:users/first_name :user_first_name]
              [:users/last_name :user_last_name]
              [:users/active :user_active]
              [:users/created_at :user_created_at]
              [:users/updated_at :user_updated_at])
      (from :bank_accounts)
      (join :users [:= :bank_accounts/user_id :users/id])))

(defn update-bank-account
  [id user-id name bank-name updated-at]
  (-> (update :bank_accounts)
      (set {:name name :bank_name bank-name :updated_at updated-at})
      (where [:= :id id] [:= :user_id user-id])
      (format)))
