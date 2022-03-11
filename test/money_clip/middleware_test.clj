(ns money-clip.middleware-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [ring.mock.request :as mock]
   [buddy.sign.jwt :as jwt]
   [duct.middleware.buddy :as buddy]
   [integrant.core :as ig]
   [money-clip.middleware :as middleware]))

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
