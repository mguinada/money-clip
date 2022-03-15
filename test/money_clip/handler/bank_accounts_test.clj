(ns money-clip.handler.bank-accounts-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [integrant.core :as ig]
            [money-clip.mock :as mock]
            [shrubbery.core :as sh]
            [money-clip.errors :as e]
            [money-clip.persistence.users :as users]
            [money-clip.persistence.bank-accounts :as bank-accounts]
            [money-clip.handler.users]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]
            [money-clip.utils :as ut]))

(st/instrument)
;; (e/uniqueness-violation-error "Email is taken" ::email-taken {:attribute :email :value "jdoe@doe.net"})
(deftest create-bank-account-test
  (let [data {:name "Savings" :bank-name "IBANK"}
        user (u/user 1 "john.doe@doe.net" "John" "Doe")
        bank-account (ba/new-bank-account user "Savings" "IBANK")]
    (testing "when a bank account with the given name does not yet exists"
      (let [db (sh/mock users/Users {:find-user-by-id user} 
                        bank-accounts/BankAccounts {:create-bank-account (assoc bank-account ::ba/id 1) :find-bank-account-by-user-and-name nil})
            handler (ig/init-key :money-clip.handler.bank-accounts/create {:db db})
        response (handler (-> (mock/request :post "/bank-accounts" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
        (is (sh/received? db users/find-user-by-id (list (::u/id user))) "reads the user")
        (is (sh/received? db bank-accounts/create-bank-account (list bank-account)) "Creates the bank account")
        (is (= :ataraxy.response/created (first response)) "HTTP response")
        (is (= "/bank-accounts/1" (second response)) "returns the path")
        (is (= {:bank-account (-> bank-account
                                  (assoc ::ba/id 1)
                                  (dissoc ::ba/user)
                                  (assoc :_links {:self "/bank-accounts/1" :users (str "/users/" (::u/id user))})
                                  ut/unqualify-keys)}
               (nth response 2)) "returns the bank account")))
    (testing "when a bank account with the given name already exists"
      (let [db (sh/mock users/Users 
                        {:find-user-by-id user}
                        bank-accounts/BankAccounts 
                        {:create-bank-account (sh/throws
                                               clojure.lang.ExceptionInfo
                                               "A bank account named `Savings` already exists"
                                               {:reason ::bank-account-name-taken :data {:attribute :name :value "Savings"}})})
            handler (ig/init-key :money-clip.handler.bank-accounts/create {:db db})
            response (handler (-> (mock/request :post "/bank-accounts" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
        (is (sh/received? db users/find-user-by-id (list (::u/id user))) "reads the user")
        (is (sh/received? db bank-accounts/create-bank-account (list bank-account)) "Tries to create the bank account")
        (is (= :ataraxy.response/precondition-failed (first response)) "HTTP response")
        (is (= {:error {:message "A bank account named `Savings` already exists" :data {:attribute :name, :value "Savings"}}} 
               (second response)) "returns an error")))))
