(ns money-clip.handler.bank-accounts-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [integrant.core :as ig]
            [money-clip.mock :as mock]
            [shrubbery.core :as sh]
            [money-clip.persistence.bank-accounts :as bank-accounts]
            [money-clip.handler.users]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]
            [money-clip.handler.restful.resources :as r]))

(st/instrument)

(deftest create-test
  (let [data {:name "Savings" :bank-name "IBANK"}
        user (u/user 1 "john.doe@doe.net" "John" "Doe")
        bank-account (ba/new-bank-account user "Savings" "IBANK")
        created-bank-account (assoc bank-account ::ba/id 1)]
    (testing "when a bank account with the given name does not yet exists"
      (let [db (sh/mock bank-accounts/BankAccounts {:create-bank-account created-bank-account :find-bank-account-by-user-and-name nil})
            handler (ig/init-key :money-clip.handler.bank-accounts/create {:db db})
            response (handler (-> (mock/request :post "/bank-accounts" data) (mock/user user)))]
        (is (sh/received? db bank-accounts/create-bank-account (list bank-account)) "Creates the bank account")
        (is (= :ataraxy.response/created (first response)) "HTTP response")
        (is (= "/bank-accounts/1" (second response)) "returns the path")
        (is (= (r/bank-account-resource created-bank-account) (nth response 2)) "returns the bank account")))))

(deftest user-bank-accounts-test
  (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
        bank-account-1 (ba/bank-account 1 user "Savings" "IBANK")
        bank-account-2 (ba/bank-account 2 user "Daily expenses" "IBANK")]
    (testing "when there user has bank accounts"
      (let [db (sh/mock bank-accounts/BankAccounts {:find-bank-accounts-by-user [bank-account-1 bank-account-2]})
            handler (ig/init-key :money-clip.handler.bank-accounts/user-bank-accounts {:db db})
            response (handler (-> (mock/request :get "/bank-accounts") (mock/user user)))]
        (is (sh/received? db bank-accounts/find-bank-accounts-by-user (list user)) "Fetches the bank accounts")
        (is (= :ataraxy.response/ok (first response)) "HTTP response")
        (is (= (map r/bank-account-resource [bank-account-1 bank-account-2]) (second response)) "Lists the bank accounts")))))

(deftest user-bank-account-test
  (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
        bank-account (ba/bank-account 1 user "Savings" "IBANK")]
    (testing "when there user has a bank account with the given id"
      (let [db (sh/mock bank-accounts/BankAccounts {:find-bank-account-by-user-and-id bank-account})
            handler (ig/init-key :money-clip.handler.bank-accounts/user-bank-account {:db db})
            response (handler (-> (mock/request :get "/bank-accounts/" {:id 1}) (mock/user user)))]
        (is (sh/received? db bank-accounts/find-bank-account-by-user-and-id (list user (::ba/id bank-account))) "Fetches the bank account")
        (is (= :ataraxy.response/ok (first response)) "HTTP response")
        (is (= (r/bank-account-resource bank-account) (second response)) "Serves the bank account")))
    (testing "when there user does not have bank account with the given id"
      (let [db (sh/mock bank-accounts/BankAccounts {:find-bank-account-by-user-and-id nil})
            handler (ig/init-key :money-clip.handler.bank-accounts/user-bank-account {:db db})
            response (handler (-> (mock/request :get "/bank-accounts/" {:id 1}) (mock/user user)))]
        (is (sh/received? db bank-accounts/find-bank-account-by-user-and-id (list user (::ba/id bank-account))) "Fetches the bank account")
        (is (= :ataraxy.response/not-found (first response)) "HTTP response")))))
