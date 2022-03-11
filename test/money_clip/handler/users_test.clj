(ns money-clip.handler.users-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [integrant.core :as ig]
            [money-clip.mock :as mock]
            [shrubbery.core :as sh]
            [money-clip.persistence.users :as users]
            [money-clip.handler.users]
            [money-clip.model.user :as u]
            [money-clip.utils :as ut]))

(st/instrument)

(deftest create-user-test
  (let [data {:email "john.doe@doe.net" :password "pa66w0rd" :password-confirmation "pa66w0rd" :first-name "John" :last-name "Doe"}
        user (-> data (assoc :id 1) (dissoc :password-confirmation) (ut/qualify-keys 'money-clip.model.user))
        db (sh/mock users/Users {:create-user user})
        handler (ig/init-key :money-clip.handler.users/create {:db db})
        response (handler (-> (mock/request :post "/users" data)))]
    (is (sh/received? db users/create-user (list (apply u/new-user (vals (dissoc data :password-confirmation))) (:password-confirmation data))) "creates the user")
    (is (= :ataraxy.response/created (first response)) "HTTP response")
    (is (= "/users/1" (second response)) "returns the path")
    (is (= {:user (-> user (dissoc ::u/password) ut/unqualify-keys)} (nth response 2)) "returns the user")))

(deftest login-test
  (testing "when the provided credentials are valid"
    (let [data {:email "john.doe@doe.net" :password "pa66w0rd"}
          user (-> data (assoc :id 1) (ut/qualify-keys 'money-clip.model.user))
          db (sh/mock users/Users {:authenticate-user user})
          handler (ig/init-key :money-clip.handler.users/login {:db db})
          response (handler (-> (mock/request :post "/login" data)))]
      (is (sh/received? db users/authenticate-user (vals data)) "Authenticates the user")
      (is (= :ataraxy.response/ok (first response)) "HTTP response")
      (is (ut/not-blank? (get-in (second response) [:user :auth-token])) "Returns the user with an auth token")))
  (testing "when the provided credetials are invalid"
    (let [data {:email "john.doe@doe.net" :password "wrong-password"}
          db (sh/mock users/Users {:authenticate-user nil})
          handler (ig/init-key :money-clip.handler.users/login {:db db})
          response (handler (-> (mock/request :post "/login" data)))]
      (is (sh/received? db users/authenticate-user (vals data)) "Authenticates the user")
      (is (= :ataraxy.response/forbidden (first response)) "HTTP response"))))
