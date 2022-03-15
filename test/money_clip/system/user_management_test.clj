(ns money-clip.system.user-management-test
  (:require [clojure.test :refer [deftest testing is] :as t]
            [money-clip.http :as http :refer [POST]]
            [money-clip.persistence.db :as db]
            [clojure.spec.test.alpha :as st]))

(st/instrument)

(t/use-fixtures :once db/init)
(t/use-fixtures :each db/cleanup)

(deftest user-creation-test
  (testing "when the email is not yet taken and the password and its confirmation match"
    (let [response (POST @db/app "/users" {:email "mguinada@gmail.com"
                                           :password "pa66word"
                                           :password-confirmation "pa66word"
                                           :first-name "Miguel"
                                           :last-name "Guinada"})]
      (is (= 201 (http/status response)) "Creates the user")))
  (testing "when the email is already taken"
    (let [response (POST @db/app "/users" {:email "mguinada@gmail.com"
                                           :password "pa66word"
                                           :password-confirmation "pa66word"
                                           :first-name "Miguel"
                                           :last-name "Guinada"})]
      (is (= 412 (http/status response)) "Serves a 412 HTTP status code")
      (is (= "Email already taken" (http/body response :error :message)) "Returns an error message")))
  (testing "when the password and confirmation don't match"
    (let [response (POST @db/app "/users" {:email "mguinada@gmail.com"
                                           :password "pa66word"
                                           :password-confirmation "no-match"
                                           :first-name "Miguel"
                                           :last-name "Guinada"})]
      (is (= 412 (http/status response)) "Serves a 412 HTTP status code")
      (is (= "Passwords don't match" (http/body response :error :message)) "Returns an error message"))))
