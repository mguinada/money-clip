(ns money-clip.model.user
  (:require [clojure.spec.alpha :as s]
            [buddy.hashers :as hs]
            [money-clip.utils :as u]))

(s/def ::id (s/or :int nat-int? :nil nil?))
(s/def ::email u/email?)
(s/def ::password (s/and string? #(>= (count %) 8)))
(s/def ::first-name (s/and string? u/required))
(s/def ::last-name (s/and string? u/required))
(s/def ::active boolean?)
(s/def ::created-at (s/or :inst inst? :nil nil?))
(s/def ::updated-at (s/or :inst inst? :nil nil?))

(s/def ::user (s/keys :req [::email ::first-name ::last-name ::active] :opt [::id ::password ::auth-token ::created-at ::updated-at]))

(defn user
  "Creates a user model.

   id: the user's persitence identifier
   email: the user's email
   password: (optional) sometimes we don't want to include the user's hashed password
   fist-name: the user's first name
   last-name: the user's last name
   active: (optinal) flag that mark a user as active defaults to true
   created-at: (optional) usualy set by the persistence layer
   updated-at: (optional) usualy set by the persistence layer
   "
  ([id email first-name last-name]
   (user id email first-name last-name true nil nil))
  ([id email first-name last-name active]
   (user id email first-name last-name active nil nil))
  ([id email password first-name last-name active created-at updated-at]
   (-> (user id email first-name last-name active created-at updated-at)
       (assoc ::password password)))
  ([id email first-name last-name active created-at updated-at]
   {::id id
    ::email email
    ::first-name first-name
    ::last-name last-name
    ::active active
    ::created-at created-at
    ::updated-at updated-at}))

(defn new-user
  "Creates a new user.

   email: the user's email
   password: a previsouly hashed password
   fist-name: the user's first name
   last-name: the user's last name
   active: (optinal) flag that mark a user as active, defaults to true
   "
  ([email password first-name last-name]
   (new-user email password first-name last-name true))
  ([email password first-name last-name active]
   (-> (user nil email password first-name last-name active nil nil)
       (dissoc ::id ::created-at ::updated-at))))

(defn active?
  "Returns `true` is the user is active, `false` otherwise."
  [{active ::active}]
  (true? active))

(defn authenticate
  "Authenticates a user.

   If the provided password is valid, it returns the user
   otherwise it returns `nil`.
   "
  [{password-digest ::password :as user} password]
  (if (hs/check password password-digest)
    (dissoc user ::password)
    nil))

(s/fdef user
  :args (s/alt
         :arity-4 (s/cat
                   :id ::id
                   :email ::email
                   :first-name ::first-name
                   :last-name ::last-name)
         :arity-5 (s/cat
                   :id ::id
                   :email ::email
                   :first-name ::first-name
                   :last-name ::last-name
                   :active ::active)
         :arity-7 (s/cat
                   :id ::id
                   :email ::email
                   :first-name ::first-name
                   :last-name ::last-name
                   :active ::active
                   :created-at ::created-at
                   :updated-at ::updated-at)
         :arity-8 (s/cat
                   :id ::id
                   :email ::email
                   :password ::password
                   :first-name ::first-name
                   :last-name ::last-name
                   :active ::active
                   :created-at ::created-at
                   :updated-at ::updated-at))
  :ret ::user)

(s/fdef new-user
  :args (s/alt
         :arity-4 (s/cat
                   :email ::email
                   :password ::password
                   :first-name ::first-name
                   :last-name ::last-name)
         :arity-5 (s/cat
                   :email ::email
                   :password ::password
                   :first-name ::first-name
                   :last-name ::last-name
                   :active ::active))
  :ret ::user)

(s/fdef active?
  :args (s/cat :user ::user)
  :ret (s/cat :boolean boolean?))

(s/fdef authenticate
  :args (s/cat
         :user ::user
         :password string?)
  :ret (s/or :user ::user :nil nil?))
