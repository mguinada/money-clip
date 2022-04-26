(ns money-clip.duct.middleware-test
  (:require [clojure.test :refer [deftest testing is]]
            [ring.mock.request :as mock]
            [shrubbery.core :as sh]
            [buddy.sign.jwt :as jwt]
            [duct.middleware.buddy :as buddy]
            [integrant.core :as ig]
            [money-clip.errors :as e]
            [money-clip.duct.middleware :as middleware]
            [money-clip.model.user :as u]
            [money-clip.persistence.users :as users]))

(defn- app-handler [{:keys [user]}]
  {:status 200, :headers {}, :body {:user user}})

(deftest authorize-test
  (let [jwt-secret "secrettobekeptveryveryverysecret"
        user (u/user 1 "john.doe@doe.net" "John" "Doe")
        middleware-authentication (ig/init-key ::buddy/authentication
                                               {:backend :jwe
                                                :secret jwt-secret})
        middleware-authorization (ig/init-key ::buddy/authorization
                                              {:backend :jwe
                                               :secret jwt-secret})
        token (jwt/encrypt (select-keys user [::u/id ::u/email]) jwt-secret)]
    (testing "when the user is authorized"
      (let [db (sh/mock users/Users {:find-user-by-id user})
            middleware-authorize (ig/init-key ::middleware/authorize {:db db})
            handler (-> app-handler
                        middleware-authorize
                        middleware-authentication
                        middleware-authorization)
            response (handler (-> (mock/request :get "/") (mock/header "Authorization" (str "Token " token))))]
        (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
        (is (= {:status 200, :headers {}, :body {:user user}} response) "Serves a 200 HTTP status code")))
    (testing "when the user is not found"
      (let [db (sh/mock users/Users {:find-user-by-id nil})
            middleware-authorize (ig/init-key ::middleware/authorize {:db db})
            handler (-> app-handler
                        middleware-authorize
                        middleware-authentication
                        middleware-authorization)
            response (handler (-> (mock/request :get "/") (mock/header "Authorization" (str "Token " token))))]
        (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Tries to fetch the user")
        (is (= {:status 401, :headers {}, :body "Unauthorized"} response) "Serves a 401 HTTP status code")))
    (testing "when the auth token is invalid"
      (let [db (sh/mock users/Users {:find-user-by-id user})
            middleware-authorize (ig/init-key ::middleware/authorize {:db db})
            handler (-> app-handler
                        middleware-authorize
                        middleware-authentication
                        middleware-authorization)
            response (handler (-> (mock/request :get "/") (mock/header "Authorization" "Token invalid-token")))]
        (is (not (sh/received? db users/find-user-by-id)) "Does not try to fetch the user")
        (is (= {:status 401, :headers {}, :body "Unauthorized"} response) "Serves a 401 HTTP status code")))))

(deftest error-handler-test
  (testing "when the error can be translated into a response"
    (let [error (e/uniqueness-violation-error "Value must be unique" ::value-not-unique {:attribute :name :value "Taken"})
          erroring-handler (fn [_] (throw error))
          middleware (ig/init-key ::middleware/error-handler {})
          handler (-> erroring-handler middleware)]
      (is (= {:status 412 :headers {} :body (e/ex-response error)} (handler (mock/request :put "/" {}))))))
  (testing "when the error can not be translated into a response"
    (let [error (e/fatal-error "Something went wrong" ::something-went-wrong)
          erroring-handler (fn [_] (throw error))
          middleware (ig/init-key ::middleware/error-handler {})
          handler (-> erroring-handler middleware)]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Something went wrong" (handler (mock/request :put "/" {})))))))
