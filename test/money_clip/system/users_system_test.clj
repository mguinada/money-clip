(ns money-clip.system.users-system-test
  (:require [clojure.test :refer [deftest testing is] :as t]
            [money-clip.http :as http :refer [POST]]
            [money-clip.system :as system]
            [money-clip.utils :as ut]
            [clojure.spec.test.alpha :as st]))

(st/instrument)

(t/use-fixtures :once system/init)
(t/use-fixtures :each system/cleanup)

(deftest post-user-test
  (testing "when the email is not yet taken and the password and its confirmation match"
    (let [response (POST @system/app "/users" {:email "john.doe@doe.net"
                                               :password "pa66word"
                                               :password-confirmation "pa66word"
                                               :first-name "John"
                                               :last-name "Doe"})]
      (is (= 201 (http/status response)) "Creates the user")
      (is (= "john.doe@doe.net" (http/body response :user :email)) "Returns the user")))
  (testing "when the email is already taken"
    (let [response (POST @system/app "/users" {:email "john.doe@doe.net"
                                               :password "pa66word"
                                               :password-confirmation "pa66word"
                                               :first-name "John"
                                               :last-name "Doe"})]
      (is (= 412 (http/status response)) "Serves a 412 HTTP status code")
      (is (= "Email already taken" (http/body response :error :message)) "Returns an error message")))
  (testing "when the password and the password confirmation don't match"
    (let [response (POST @system/app "/users" {:email "john.doe@doe.net"
                                               :password "pa66word"
                                               :password-confirmation "no-match"
                                               :first-name "John"
                                               :last-name "Doe"})]
      (is (= 412 (http/status response)) "Serves a 412 HTTP status code")
      (is (= "Passwords don't match" (http/body response :error :message)) "Returns an error message"))))

(deftest post-login-test
  (let [_ (POST @system/app "/users" {:email "john.doe@doe.net"
                                      :password "pa66word"
                                      :password-confirmation "pa66word"
                                      :first-name "John"
                                      :last-name "Doe"})]
    (testing "when the login is valid"
      (let [response (POST @system/app "/login" {:email "john.doe@doe.net" :password "pa66word"})]
        (is (= 200 (http/status response)) "Serves a 412 HTTP status code")
        (is (ut/not-blank? (http/body response :user :auth-token)) "Returns an authorization token")))
    (testing "when the login is invalid"
      (let [response (POST @system/app "/login" {:email "john.doe@doe.net" :password "wrong-password"})]
        (is (= 401 (http/status response)) "Serves a 401 HTTP status code")
        (is (= "Unauthorized" (http/body response :error :message)) "Returns an error message")))))
