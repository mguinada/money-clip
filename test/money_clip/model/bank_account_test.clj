(ns money-clip.model.bank-account-test
  (:require
   [clojure.test :refer [deftest is]]
   [clojure.spec.test.alpha :as st]
   [money-clip.model.user :as u]
   [money-clip.model.bank-account :as a]))

(st/instrument)

(deftest bank-account-test
  (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
        bank-account (a/bank-account 1 user "Daily expenses" "IBANK")]
    (is (= 1 (::a/id bank-account)))
    (is (= user (::a/user bank-account)))
    (is (= "Daily expenses" (::a/name bank-account)))
    (is (= "IBANK" (::a/bank-name bank-account)))))
