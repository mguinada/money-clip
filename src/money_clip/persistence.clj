(ns money-clip.persistence
  (:require [clojure.spec.alpha :as s]))

(s/def ::db (s/keys :req-un [::spec]))

(defn serializer
  "Given a function that create a model and a list of keys
   returns a function to serialize a model

   example:

   (def to-model (p/serializer u/user :id :email :first_name :last_name :active :created_at :updated_at))

   Will apply the provided keyword values of any given map to the `u/user` function.
   "
  [f & keys]
  (fn [[result]]
    (if-not (nil? result)
      (apply f (-> result (select-keys keys) vals))
      nil)))

(s/fdef model-serializer
  :args (s/cat :fn fn? :keys coll?)
  :ret (s/or :map map? :nil nil?))
