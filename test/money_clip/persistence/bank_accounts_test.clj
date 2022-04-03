(ns money-clip.persistence.bank-accounts-test
  (:require [clojure.test :refer [deftest testing is] :as t]
            [clojure.spec.test.alpha :as st]
            [money-clip.system :as system]
            [money-clip.persistence.users :as users]
            [money-clip.persistence.bank-accounts :as bank-accounts]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]))

(st/instrument)

(t/use-fixtures :once system/init)
(t/use-fixtures :each system/cleanup)

(deftest create-bank-account-test
  (let [user (users/create-user @system/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") "pa66w0rd")]
   (testing "when a bank account with the given name doesn't yet exist"
    (let [bank-account (bank-accounts/create-bank-account @system/db (ba/new-bank-account user "Daily expenses" "IBANK"))]
      (is (nat-int? (::ba/id bank-account)))
      (is (= (dissoc user ::u/password) (::ba/user bank-account)))
      (is (= "Daily expenses" (::ba/name bank-account)))
      (is (= "IBANK" (::ba/bank-name bank-account)))
      (is (inst? (::ba/created-at bank-account)))
      (is (inst? (::ba/updated-at bank-account)))))
  (testing "when a bank account with the given name already exists"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"A bank account named `Daily expenses` already exists"
                          (bank-accounts/create-bank-account @system/db (ba/new-bank-account user "Daily expenses" "IBANK"))) "If the case matches")
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"A bank account named `Daily expenses` already exists"
                          (bank-accounts/create-bank-account @system/db (ba/new-bank-account user "Daily EXPENSES" "IBANK")))) "If the case doesn't match")))

(deftest find-bank-accounts-by-user-test
  (let [john (users/create-user @system/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") "pa66w0rd")
        jane (users/create-user @system/db (u/new-user "jane.doe@doe.net" "pa66w0rd" "Jane" "Doe") "pa66w0rd")
        bank-accounts (mapv (partial bank-accounts/create-bank-account @system/db)
                            [(ba/new-bank-account john "Daily expenses" "IBANK")
                             (ba/new-bank-account john "Savings" "UBANK")
                             (ba/new-bank-account jane "Income" "UBANK")])]
    (testing "John's accounts"
      (is (= (take 2 bank-accounts) (bank-accounts/find-bank-accounts-by-user @system/db john))))
    (testing "Jane's accounts"
    (is (= (take-last 1 bank-accounts) (bank-accounts/find-bank-accounts-by-user @system/db jane))))))

(deftest find-bank-account-by-user-and-name-test
  (let [john (users/create-user @system/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") "pa66w0rd")
        jane (users/create-user @system/db (u/new-user "jane.doe@doe.net" "pa66w0rd" "Jane" "Doe") "pa66w0rd")
        bank-accounts (mapv (partial bank-accounts/create-bank-account @system/db)
                            [(ba/new-bank-account john "Daily expenses" "IBANK")
                             (ba/new-bank-account john "Savings" "UBANK")
                             (ba/new-bank-account jane "Savings" "UBANK")])]
    (testing "when there's a bank account with the given name for the user"
      (is (= (second bank-accounts) (bank-accounts/find-bank-account-by-user-and-name @system/db john "Savings")) "If the case matches")
      (is (= (second bank-accounts) (bank-accounts/find-bank-account-by-user-and-name @system/db john "SaVinGs")) "If the case dosen't match"))
    (testing "when there's no bank account with the given name for the user"
      (is (nil? (bank-accounts/find-bank-account-by-user-and-name @system/db jane "Daily expenses"))))))

(deftest find-bank-account-by-user-and-id-test
  (let [john (users/create-user @system/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") "pa66w0rd")
        jane (users/create-user @system/db (u/new-user "jane.doe@doe.net" "pa66w0rd" "Jane" "Doe") "pa66w0rd")
        bank-accounts (mapv (partial bank-accounts/create-bank-account @system/db)
                            [(ba/new-bank-account john "Daily expenses" "IBANK")
                             (ba/new-bank-account john "Savings" "UBANK")
                             (ba/new-bank-account jane "Savings" "UBANK")])]
    (testing "when there's a bank account with the given id for the user"
      (is (= (second bank-accounts) (bank-accounts/find-bank-account-by-user-and-id @system/db john (::ba/id (second bank-accounts))))))
    (testing "when there's no bank account with the given id for the user"
      (is (nil? (bank-accounts/find-bank-account-by-user-and-id @system/db jane (::ba/id (second bank-accounts))))))))

(deftest find-bank-account-by-id
  (let [user (users/create-user @system/db (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") "pa66w0rd")
        bank-account (bank-accounts/create-bank-account @system/db (ba/new-bank-account user "Daily expenses" "IBANK"))]
    (testing "when the account with the provided ID exists"
      (is (= bank-account (bank-accounts/find-bank-account-by-id @system/db (::ba/id bank-account)))))
    (testing "when the account with the provided ID does not exist"
      (is (nil? (bank-accounts/find-bank-account-by-id @system/db 1))))))
