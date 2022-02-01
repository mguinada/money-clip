(ns money-clip.utils
  "A collection of convenience functions"
  (:require [clojure.string :as s]))

(defprotocol Blank
  "Determines if a value is blank."
  (blank? [v]))

(extend-protocol Blank
  nil
  (blank? [_] true)
  java.lang.Object
  (blank? [_] false)
  java.lang.String
  (blank? [s] (s/blank? s))
  clojure.lang.IPersistentCollection
  (blank? [coll] (empty? coll)))

(def not-blank? (complement blank?))
(def required not-blank?)

(def ^:private email-regexp
  #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(defn email?
  "Returns true if `s` is a valid email address, false otherwise."
  [s]
  (->> s
       str
       (re-matches email-regexp)
       boolean))
