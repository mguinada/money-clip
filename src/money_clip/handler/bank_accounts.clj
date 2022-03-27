(ns money-clip.handler.bank-accounts
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]
            [money-clip.persistence.users :as users]
            [money-clip.persistence.bank-accounts :as bank-accounts]
            [money-clip.handler.restful.resources :as r]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ name bank-name] :ataraxy/result {user-id ::u/id} :identity}]
    (let [user (users/find-user-by-id db user-id)
          bank-account (bank-accounts/create-bank-account db (ba/new-bank-account user name bank-name))]
      [::response/created
       (str "/bank-accounts/" (::ba/id bank-account))
       (r/bank-account-resource bank-account)])))

(defmethod ig/init-key ::user-bank-accounts [_ {:keys [db]}]
  (fn [{{user-id ::u/id} :identity}]
    (if-let [user (users/find-user-by-id db user-id)]
      [::response/ok (map r/bank-account-resource (bank-accounts/find-bank-accounts-by-user db user))]
      [::response/not-found])))

(defmethod ig/init-key ::user-bank-account [_ {:keys [db]}]
  (fn [{[_ id] :ataraxy/result {user-id ::u/id} :identity}]
    (if-let [user (users/find-user-by-id db user-id)]
      (if-let [bank-account (bank-accounts/find-bank-account-by-user-and-id db user id)]
        [::response/ok (r/bank-account-resource bank-account)]
        [::response/not-found])
      [::response/not-found])))
