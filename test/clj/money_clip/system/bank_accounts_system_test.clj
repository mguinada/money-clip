(ns money-clip.system.bank-accounts-system-test
  (:require [clojure.test :refer [deftest testing is] :as t]
            [money-clip.http :as http :refer [POST]]
            [money-clip.system :as system]
            [clojure.spec.test.alpha :as st]
            [ataraxy.response :as response]))

(st/instrument)

(t/use-fixtures :once system/init)
(t/use-fixtures :each system/cleanup)

(deftest post-bank-account-test
  (let [[_ auth-token] (system/create-user-and-login @system/app "test.user@users.com")]
    (testing "when the bank account name is not taken"
      (let [response (POST @system/app "/api/bank-accounts" {:name "Savings" :bank_name "IBANK"} :headers {"Authorization" (str "Token " auth-token)})]
        (is (= 201 (http/status response)) "Creates the bank account")
        (is (= {:name "Savings" :bank_name "IBANK"} (-> (http/body response) :bank_account (select-keys [:name :bank_name]))) "Returns the payload")))
    (testing "when the bank account name is taken"
      (let [response (POST @system/app "/api/bank-accounts" {:name "Savings"} :headers {"Authorization" (str "Token " auth-token)})]
        (is (= 412 (http/status response)) "Serves an HTTP status code")
        (is (= "A bank account named `Savings` already exists" (http/body response :error :message)) "Returns an error message")))
    (testing "when the user the authentication token is invalid"
      (let [response (POST @system/app "/api/bank-accounts" {:name "Savings"} :headers {"Authorization" (str "Token " "fake-auth-token")})]
        (is (= 401 (http/status response)) "Serves an HTTP status code")
        (is (= "Invalid credentials" (http/body response :error :message)) "Returns an error message")))))
