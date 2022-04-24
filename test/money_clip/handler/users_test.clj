(ns money-clip.handler.users-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [integrant.core :as ig]
            [money-clip.mock :as mock]
            [shrubbery.core :as sh]
            [money-clip.persistence.users :as users]
            [money-clip.handler.users]
            [money-clip.model.user :as u]
            [money-clip.handler.restful.resources :as r]
            [money-clip.utils :as ut]))

(st/instrument)

(deftest create-test
  (let [data {:email "john.doe@doe.net" :password "pa66w0rd" :password-confirmation "pa66w0rd" :first-name "John" :last-name "Doe"}
        user (-> data (assoc :id 1) (dissoc :password-confirmation) (ut/qualify-keys 'money-clip.model.user))]
    (testing "when the user's email has not been yet taken"
      (let [db (sh/mock users/Users {:create-user user})
            handler (ig/init-key :money-clip.handler.users/create {:db db})
            response (handler (-> (mock/request :post "/users" data)))]
        (is (sh/received? db users/create-user (list
                                                (apply u/new-user (vals (dissoc data :password-confirmation)))
                                                (:password-confirmation data))) "creates the user")
        (is (= :ataraxy.response/created (first response)) "HTTP response")
        (is (= "/users/1" (second response)) "returns the path")
        (is (= (r/user-resource user) (nth response 2)) "returns the user")))))

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
      (is (= :ataraxy.response/unauthorized (first response)) "HTTP response"))))

(deftest user-test
  (testing "when the user with the provided id exists"
    (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
          db (sh/mock users/Users {:find-user-by-id user})
          handler (ig/init-key :money-clip.handler.users/user {:db db})
          response (handler (-> (mock/request :get "/user") (mock/identity (select-keys user [::u/id ::u/email]))))]
      (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
      (is (= :ataraxy.response/ok (first response)) "HTTP response")
      (is (= (r/user-resource user) (second response)) "Returns the user")))
  (testing "when the user with the provided id does not exist"
    (let [db (sh/mock users/Users {:find-user-by-id nil})
          handler (ig/init-key :money-clip.handler.users/user {:db db})
          response (handler (-> (mock/request :get "/user") (mock/identity {::u/id 999})))]
      (is (sh/received? db users/find-user-by-id [999]) "Fetches the user")
      (is (= :ataraxy.response/not-found (first response)) "HTTP response")
      (is (nil? (second response)) "Does not return the user")))
  (testing "when the user is unidentified"
    (let [db (sh/mock users/Users {:find-user-by-id nil})
          handler (ig/init-key :money-clip.handler.users/user {:db db})
          response (handler (-> (mock/request :get "/user")))]
      (is (sh/received? db users/find-user-by-id) "Tries to fetch the user")
      (is (= :ataraxy.response/not-found (first response)) "HTTP response"))))

(deftest update-test
  (testing "when the user is updated"
    (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
          data {:first-name "Joe" :last-name "Moe"}
          updated-user (merge user (ut/qualify-keys data 'money-clip.model.user))
          db (sh/mock users/Users {:find-user-by-id user :update-user updated-user})
          handler (ig/init-key :money-clip.handler.users/update {:db db})
          response (handler (-> (mock/request :put "/user" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
      (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
      (is (sh/received? db users/update-user [updated-user]) "Updates the user")
      (is (= :ataraxy.response/ok (first response)) "HTTP response")
      (is (= (r/user-resource updated-user) (second response)) "Returns the user")))
  (testing "when the user is not found"
    (let [user (u/user 1 "john.doe@doe.net" "John" "Doe")
          data {:first-name "Joe" :last-name "Moe"}
          db (sh/mock users/Users {:find-user-by-id nil})
          handler (ig/init-key :money-clip.handler.users/update {:db db})
          response (handler (-> (mock/request :put "/user" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
      (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
      (is (not (sh/received? db users/update-user)) "Does not update the user")
      (is (= :ataraxy.response/not-found (first response)) "HTTP response")
      (is (nil? (second response)) "Does not returns the user"))))

(deftest update-password-test
  (testing "when the user password is updated"
    (let [user (-> (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") (assoc ::u/id 1))
          updated-user (assoc user ::u/password "n3w-pa660rd")
          data {:current-password "pa66w0rd" :new-password "n3w-pa660rd" :new-password-confirmaiton "n3w-pa660rd"}
          db (sh/mock users/Users {:find-user-by-id user :update-user-password updated-user})
          handler (ig/init-key :money-clip.handler.users/change-password {:db db})
          response (handler (-> (mock/request :put "/user/change-password" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
      (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
      (is (sh/received? db users/update-user-password (concat [user] (vals data))) "Updates the user's password")
      (is (= :ataraxy.response/ok (first response)) "HTTP response")
      (is (= (r/user-resource updated-user) (second response)) "Returns the user")))
  (testing "when the user is not found"
    (let [user (-> (u/new-user "john.doe@doe.net" "pa66w0rd" "John" "Doe") (assoc ::u/id 1))
          data {:current-password "pa66w0rd" :new-password "n3w-pa660rd" :new-password-confirmaiton "n3w-pa660rd"}
          db (sh/mock users/Users {:find-user-by-id nil :update-user-password nil})
          handler (ig/init-key :money-clip.handler.users/change-password {:db db})
          response (handler (-> (mock/request :put "/user/change-password" data) (mock/identity (select-keys user [::u/id ::u/email]))))]
      (is (sh/received? db users/find-user-by-id [(::u/id user)]) "Fetches the user")
      (is (not (sh/received? db users/update-user-password)) "Does not update the password")
      (is (= :ataraxy.response/not-found (first response)) "HTTP response")
      (is (nil? (second response)) "Does not returns the user"))))
