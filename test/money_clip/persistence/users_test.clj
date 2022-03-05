(ns money-clip.persistence.users-test
  (:require [clojure.test :refer [deftest testing is] :as t]
            [clojure.spec.test.alpha :as st]
            [money-clip.persistence.db :as db]
            [money-clip.persistence.users :as users]
            [money-clip.model.user :as u]))

(st/instrument)

(t/use-fixtures :once db/init)
(t/use-fixtures :each db/cleanup)

(deftest create-user-test
  (let [user (u/new-user "john.doe@doe.net" "pa6w00rd" "John" "Doe")]
    (testing "when the user's email is not already in use"
      (is (= "john.doe@doe.net" (::u/email (users/create-user @db/db user)))))
    (testing "when the user's email is already in use"
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Email already taken" (users/create-user @db/db user))))))

(deftest find-user-by-id-test
  (testing "when the user with the given id exists"
    (let [new-user (u/new-user "john.doe@doe.net" "pa6w00rd" "John" "Doe")
          user (users/create-user @db/db new-user)]
      (is (= user (users/find-user-by-id @db/db (::u/id user))))))
  (testing "when the user with the given id doesn't exist"
    (is (nil? (users/find-user-by-id @db/db 1)))))

(deftest find-user-by-email-test
  (testing "when the user with the given email exists"
    (let [new-user (u/new-user "john.doe@doe.net" "pa6w00rd" "John" "Doe")
          user (users/create-user @db/db new-user)]
      (is (= user (users/find-user-by-email @db/db "john.doe@doe.net")))))
  (testing "when the user with the given email doesn't exist"
    (is (nil? (users/find-user-by-email @db/db "jane.doe@doe.net")))))

(deftest authenticate-user-test
  (let [user (users/create-user @db/db (u/new-user "john.doe@doe.net" "pa6w00rd" "John" "Doe"))
        _ (users/create-user @db/db (u/new-user "jane.doe@doe.net" "pa6w00rd" "John" "Doe" false))]
    (testing "when the email and password are valid credentials"
      (is (= (dissoc user ::u/password) (users/authenticate-user @db/db "john.doe@doe.net" "pa6w00rd"))) "Authenticates the user")
    (testing "when a user with a given email is not found"
      (is (nil? (users/authenticate-user @db/db "james.doe@doe.net" "pa6w00rd"))) "Returns nil")
    (testing "when a user with a given email is found but the passwors is invalid"
      (is (nil? (users/authenticate-user @db/db "john.doe@doe.net" "invalid-passwd"))) "Returns nil")
    (testing "when the email and password are valid credentials yet the user is not active"
      (is (nil? (users/authenticate-user @db/db "jane.doe@doe.net" "pa6w00rd"))) "Returns nil")))
