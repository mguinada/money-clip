(ns money-clip.model.bank-account
  (:require [clojure.spec.alpha :as s]
            [money-clip.utils :as ut]
            [money-clip.model.user :as u]))

(s/def ::id (s/or :int nat-int? :nil nil?))
(s/def ::user ::u/user)
(s/def ::user-id (s/and nat-int? ut/required))
(s/def ::name (s/and string? ut/required))
(s/def ::bank-name (s/or :string string? :blank ut/blank?))
(s/def ::created-at (s/or :inst inst? :nil nil?))
(s/def ::updated-at (s/or :inst inst? :nil nil?))

(s/def ::bank-account (s/keys :req [(or ::user ::user-id) ::name ::bank-name] :opt [::id ::created-at ::updated-at]))

(defn bank-account
  ([id user name]
   (bank-account id user name "" nil nil))
  ([id user name bank-name]
   (bank-account id user name bank-name nil nil))
  ([id user name bank-name created-at updated-at]
   {::id id
    ::user user
    ::name name
    ::bank-name bank-name
    ::created-at created-at
    ::updated-at updated-at}))

(defn new-bank-account
  ([user name]
   (new-bank-account user name ""))
  ([user name bank-name]
   (-> (bank-account nil user name bank-name)
       (dissoc ::id ::created-at ::updated-at))))

(s/fdef bank-account
  :args (s/alt
         :arity-3 (s/cat
                   :id ::id
                   :user ::user
                   :name ::name)
         :arity-4 (s/cat
                   :id ::id
                   :user ::user
                   :name ::name
                   :bank-name ::bank-name)
         :arity-6 (s/cat
                   :id ::id
                   :user ::user
                   :name ::name
                   :bank-name ::bank-name
                   :created-at ::created-at
                   :updated-at ::updated-at))
  :ret ::bank-account)

(s/fdef new-bank-account
  :args (s/alt
         :arity-2 (s/cat
                   :user ::user
                   :name ::name)
         :arity-3 (s/cat
                   :user ::user
                   :name ::name
                   :bank-name ::bank-name))
  :ret ::bank-account)
