(ns money-clip.persistence
  (:require [clojure.spec.alpha :as s]
            [tick.core :as t]
            [money-clip.utils :as ut]))

(s/def ::db (s/keys :req-un [::spec]))

(def underscore-keys (partial ut/map-keys ut/underscore))

(defn timestamp
  []
  (-> (System/currentTimeMillis)
      (java.sql.Timestamp.)))

(defn check-spec!
  "Checks if map `m` conforms to spec `s`."
  [s m]
  (if (= ::s/invalid (s/conform s m))
    (throw (ex-info "Invalid entity"
                    {:type ::spec-violation
                     :spec s
                     :cause (s/explain-str s m)
                     :explain (s/explain-data s m)
                     :invalid-data m}))
    true))

(defn serializer
  "Given a function that create a model and a list of keys
   returns a function to serialize a model

   example:

   (def to-model (p/serializer u/user :id :email :first_name :last_name :active :created_at :updated_at))

   Will apply the provided keyword values of any given map to the `u/user` function."
  [f & keys]
  (fn [[result]]
    (if-not (nil? result)
      (apply f (-> result (select-keys keys) vals))
      nil)))

(s/fdef model-serializer
  :args (s/cat :fn fn? :keys coll?)
  :ret (s/or :map map? :nil nil?))
