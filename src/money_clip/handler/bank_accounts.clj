(ns money-clip.handler.bank-accounts
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [money-clip.model.user :as u]
            [money-clip.model.bank-account :as ba]
            [money-clip.persistence.users :as users]
            [money-clip.persistence.bank-accounts :as bank-accounts]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ name bank-name] :ataraxy/result {user-id ::u/id} :identity}]
    (let [user (users/find-user-by-id db user-id)
          bank-account (bank-accounts/create-bank-account db (ba/new-bank-account user name bank-name))]
      [::response/created
       (str "/bank-accounts/" (::ba/id bank-account))
       {:bank-account bank-account}])))
