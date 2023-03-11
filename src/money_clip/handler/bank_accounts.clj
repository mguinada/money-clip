(ns money-clip.handler.bank-accounts
  "BankAcount request handlers"
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [money-clip.model.bank-account :as ba]
            [money-clip.persistence.bank-accounts :as bank-accounts]
            [money-clip.handler.restful.resources :as r]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{user :user {name :name bank-name :bank-name} :body-params}]
    (let [bank-account (bank-accounts/create-bank-account db (ba/new-bank-account user name bank-name))]
      [::response/created
       (str "/bank-accounts/" (::ba/id bank-account))
       (r/bank-account-resource bank-account)])))

(defmethod ig/init-key ::user-bank-accounts [_ {:keys [db]}]
  (fn [{user :user}]
    [::response/ok (map r/bank-account-resource (bank-accounts/find-bank-accounts-by-user db user))]))

(defmethod ig/init-key ::user-bank-account [_ {:keys [db]}]
  (fn [{user :user [_ id] :ataraxy/result}]
    (if-let [bank-account (bank-accounts/find-bank-account-by-user-and-id db user id)]
      [::response/ok (r/bank-account-resource bank-account)]
      [::response/not-found])))

(defmethod ig/init-key ::update [_ {:keys [db]}]
  (fn [{user :user [_ id name bank-name] :ataraxy/result}]
    (if-let [bank-account (bank-accounts/find-bank-account-by-user-and-id db user id)]
      [::response/ok (r/bank-account-resource (bank-accounts/update-bank-account db user (assoc bank-account ::ba/name name ::ba/bank-name bank-name)))]
      [::response/not-found])))
