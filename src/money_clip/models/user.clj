(ns money-clip.models.user
  (:require [clojure.spec.alpha :as s]
            [buddy.hashers :as hs]
            [money-clip.utils :as u]))

(s/def ::id (s/or :int nat-int? :nil nil?))
(s/def ::email u/email?)
(s/def ::password (s/and string? #(>= (count %) 8)))
(s/def ::first-name (s/and string? u/required))
(s/def ::last-name (s/and string? u/required))
(s/def ::active boolean?)

(s/def ::user (s/keys :req [::id ::email ::password ::first-name ::last-name ::active]))

(defn new-user
  "Creates a new user

   email: the user's email
   password: the plain text password (it will be hashed)
   fist-name: the user's first name
   last-name: the user's last name
   "
  ([email password first-name last-name]
   (new-user nil email password first-name last-name))
  ([id email password first-name last-name]
   {::id id
    ::password (hs/derive password {:alg :pbkdf2+sha512})
    ::email email
    ::first-name first-name
    ::last-name last-name
    ::active true}))

(s/fdef new-user
        :args (s/alt
               :without-id (s/cat
                            :email ::email
                            :password ::password
                            :first-name ::first-name
                            :last-name ::last-name)
               :with-id (s/cat
                         :id ::id
                         :email ::email
                         :password ::password
                         :first-name ::first-name
                         :last-name ::last-name))
        :ret ::user)
