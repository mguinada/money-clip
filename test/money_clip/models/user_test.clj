(ns money-clip.models.user-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.spec.test.alpha :as stest]
   [money-clip.models.user :as u]))

(stest/instrument `u/new-user)

(deftest new-user-test
  (let [user (u/new-user 1 "jdoe@doe.net" "pa66w0rd" "John" "Doe")]
    (testing "creates a user"
      (is (= 1 (::u/id user)))
      (is (= "jdoe@doe.net" (::u/email user)))
      (is (= "John" (::u/first-name user)))
      (is (= "Doe" (::u/last-name user)))
      (is (true? (::u/active user))))
    (testing "hashes the password"
      (is (not= "pa66w0rd" (::u/password user))))))
