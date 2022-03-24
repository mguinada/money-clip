(ns money-clip.duct.middleware-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [ring.mock.request :as mock]
   [buddy.sign.jwt :as jwt]
   [duct.middleware.buddy :as buddy]
   [integrant.core :as ig]
   [money-clip.errors :as e]
   [money-clip.duct.middleware :as middleware]))

(defn- app-handler [{:keys [identity]}]
  {:status 200, :headers {}, :body identity})

(deftest authorize-test
  (let [jwt-secret "secrettobekeptveryveryverysecret"
        middleware-authentication (ig/init-key ::buddy/authentication
                                               {:backend :jwe
                                                :secret jwt-secret})
        middleware-authorization (ig/init-key ::buddy/authorization
                                              {:backend :jwe
                                               :secret jwt-secret})
        middleware-authorize (ig/init-key ::middleware/authorize {})
        token (jwt/encrypt {:user "jdoe"} jwt-secret)
        handler (-> app-handler
                    middleware-authorize
                    middleware-authentication
                    middleware-authorization)]
    (testing "when the user is authorized"
      (is (= {:status 200, :headers {}, :body {:user "jdoe"}}
             (handler (-> (mock/request :get "/") 
                          (mock/header "Authorization" (str "Token " token)))))))
    (testing "when the user is not authorized"
      (is (= {:status 401, :headers {}, :body "Unauthorized"}
             (handler (-> (mock/request :get "/")
                          (mock/header "authorization" (str "Token invalid-token")))))))))

(deftest error-handler-test
  (testing "when the error can be trasnlated into a response"
    (let [error (e/uniqueness-violation-error "Value must be unique" ::value-not-unique {:attribute :name :value "Taken"})
          erroring-handler (fn [_] (throw error))
          middleware (ig/init-key ::middleware/error-handler {})
          handler (-> erroring-handler middleware)]
      (is (= {:status 412 :headers {} :body (e/ex-response error)} (handler (mock/request :put "/" {}))))))
  (testing "when the error can not be trasnlated into a response"
    (let [error (e/fatal-error "Something went wrong" ::something-went-wrong)
          erroring-handler (fn [_] (throw error))
          middleware (ig/init-key ::middleware/error-handler {})
          handler (-> erroring-handler middleware)]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Something went wrong" (handler (mock/request :put "/" {})))))))
