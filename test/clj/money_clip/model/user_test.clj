(ns money-clip.model.user-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.spec.test.alpha :as st]
   [buddy.hashers :as hs]
   [money-clip.model.user :as u]))

(st/instrument)

(deftest new-user-test
  (let [user (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe")]
    (testing "creates a user"
      (is (nil? (::u/id user)))
      (is (= "john.doe@doe.net" (::u/email user)))
      (is (= "pa66w0rd" (::u/password user)))
      (is (= "John" (::u/first-name user)))
      (is (= "Doe" (::u/last-name user)))
      (is (true? (::u/active user))))))

(deftest user-test
  (let [user (u/user 1 "john.doe@doe.net" "pa66w0rd" "John" "Doe" true nil nil)]
    (testing "creates a user's model"
      (is (= 1 (::u/id user)))
      (is (= "john.doe@doe.net" (::u/email user)))
      (is (= "John" (::u/first-name user)))
      (is (= "Doe" (::u/last-name user)))
      (is (true? (::u/active user))))))

(deftest full-name-test
  (let [user (u/user 1 "john.doe@doe.net" "pa66w0rd" "John" "Doe" true nil nil)]
    (is (= "John Doe" (u/full-name user)))))

(deftest authenticate-test
  (let [user (u/new-user "john.doe@doe.net" (hs/derive "pa66w0rd" {:alg :pbkdf2+sha512})  "John" "Doe")]
    (testing "when the password is valid"
      (is (= (dissoc user ::u/password) (u/authenticate user "pa66w0rd"))))
    (testing "when the password is invalid"
      (is (nil? (u/authenticate user "invalid-pwd"))))))
