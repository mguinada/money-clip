(ns money-clip.persistence.bank-accounts-test
  (:require
   [clojure.test :refer [deftest testing is] :as t]
   [clojure.spec.test.alpha :as st]
   [money-clip.persistence.db :as db]
   [money-clip.persistence.users :as users]
   [money-clip.persistence.bank-accounts :as bank-accounts]
   [money-clip.model.user :as u]
   [money-clip.model.bank-account :as ba]))

(st/instrument)

(t/use-fixtures :once db/init)
(t/use-fixtures :each db/cleanup)

(deftest create-bank-account-test
  (let [user (users/create-user @db/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe"))
        bank-account (bank-accounts/create-bank-account @db/db (ba/new-bank-account user "Daily expenses" "IBANK"))]
    (is (nat-int? (::ba/id bank-account)))
    (is (= (dissoc user ::u/password) (::ba/user bank-account)))
    (is (= "Daily expenses" (::ba/name bank-account)))
    (is (= "IBANK" (::ba/bank-name bank-account)))
    (is (inst? (::ba/created-at bank-account)))
    (is (inst? (::ba/updated-at bank-account)))))

(deftest find-bank-accounts-by-user-test
  (let [john (users/create-user @db/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe"))
        jane (users/create-user @db/db (u/new-user "jane.doe@doe.net" "pa66w0rd" "Jane" "Doe"))
        bank-accounts (mapv (partial bank-accounts/create-bank-account @db/db)
                            [(ba/new-bank-account john "Daily expenses" "IBANK")
                             (ba/new-bank-account john "Savings" "UBANK")
                             (ba/new-bank-account jane "Income" "UBANK")])]
    (testing "John's accounts"
      (is (= (take 2 bank-accounts) (bank-accounts/find-bank-accounts-by-user @db/db john))))
    (testing "Jane's accounts"
    (is (= (take-last 1 bank-accounts) (bank-accounts/find-bank-accounts-by-user @db/db jane))))))

(deftest find-bank-account-by-id
  (let [user (users/create-user @db/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe"))
        bank-account (bank-accounts/create-bank-account @db/db (ba/new-bank-account user "Daily expenses" "IBANK"))]
    (testing "when the account with the provided ID exists"
      (is (= bank-account (bank-accounts/find-bank-account-by-id @db/db (::ba/id bank-account)))))
    (testing "when the account with the provided ID does not exist"
      (is (nil? (bank-accounts/find-bank-account-by-id @db/db 1))))))
