(ns money-clip.handler.restful.resources-test
  (:require
   [clojure.test :refer [deftest is]]
   [money-clip.model.user :as u]
   [money-clip.model.bank-account :as ba]
   [money-clip.handler.restful.resources :as r]))

(def timestamp (java.util.Date.))

(deftest user-resource-test
  (let [user (u/user 1 "john.doe@doe.net" "pa66w0rd" "John" "Doe" true timestamp timestamp)]
    (is (= {:user
            {:id 1
             :email "john.doe@doe.net"
             :first_name "John"
             :last_name "Doe"
             :created_at timestamp
             :updated_at timestamp
             :_links {:self "/api/user" :bank_accounts "/api/bank-accounts" :change_password "/api/user/change-password"}}}
           (r/user-resource user)))))

(deftest bank-account-resource-test
  (let [user (u/user 1 "john.doe@doe.net" "pa66w0rd" "John" "Doe" true timestamp timestamp)
        bank-account (ba/bank-account 1 user "Daily expenses" "IBANK" timestamp timestamp)]
    (is (= {:bank_account
            {:id 1
             :name "Daily expenses"
             :bank_name "IBANK"
             :created_at timestamp
             :updated_at timestamp
             :_links {:self "/api/bank-accounts/1" :user "/api/user"}}}
           (r/bank-account-resource bank-account)))))
