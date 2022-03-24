(ns money-clip.errors-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.spec.test.alpha :as st]
   [money-clip.errors :as e]))

(st/instrument)

(deftest uniqueness-violation-error-test
  (let [error (e/uniqueness-violation-error "Email is taken" ::email-taken {:attribute :email :value "jdoe@doe.net"})
        error-message (ex-message error)
        error-data (ex-data error)]
    (is (= "Email is taken" error-message) "the error message")
    (is (= ::e/uniqueness-violation-error (:type error-data)) "the error type")
    (is (= ::email-taken (:reason error-data)) "the error reason")
    (is (= {:attribute :email :value "jdoe@doe.net"} (:data error-data)) "the error data")
    (is (e/respondable? error))))

(deftest fatal-error-test
  (let [error (e/fatal-error "Something bad happened" ::fatal)
        error-message (ex-message error)
        error-data (ex-data error)]
    (is (= "Something bad happened" error-message) "the error message")
    (is (= ::e/fatal-error (:type error-data)) "the error type")
    (is (= ::fatal (:reason error-data)) "the error reason")
    (is (false? (e/contains-data? error)) "the error has no data")
    (is (false? (e/respondable? error)))))

(deftest ex-response-test
  (testing "when the error provides error data"
    (let [error (e/uniqueness-violation-error "Email is taken" ::email-taken {:attribute :email :value "jdoe@doe.net"})]
      (try
        (throw error)
        (catch Exception e
          (is (= {:error {:message "Email is taken" :data {:attribute :email :value "jdoe@doe.net"}}} (e/ex-response e))))))
    (let [error (e/uniqueness-violation-error "Email is taken" ::email-taken {:attribute :email})]
      (try
        (throw error)
        (catch Exception e
          (is (= {:error {:message "Email is taken" :data {:attribute :email}}} (e/ex-response e)))))))
  (testing "when the error does not provide error data"
    (let [error (e/uniqueness-violation-error "Email is taken" ::email-taken)]
      (try
        (throw error)
        (catch Exception e
          (is (= {:error {:message "Email is taken"}} (e/ex-response e))))))))

(deftest try-cath-test
  (testing "when the error is respondable"
    (let [error (e/uniqueness-violation-error "Email is taken" ::email-taken {:attribute :email :value "jdoe@doe.net"})]
      (is (= {:status 412 :headers {} :body {:error {:message "Email is taken" :data {:attribute :email :value "jdoe@doe.net"}}}}
              (e/try-catch (throw error))) "The error reponse map is returned"))))
  (testing "when the error is not respondable"
    (let [error (e/fatal-error "Something bad happened" ::fatal)]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Something bad happened" (e/try-catch (throw error))) "The exception is rethrown")))
