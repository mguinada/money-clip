(ns money-clip.utils
  "A collection of convenience functions"
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:refer-clojure :exclude [replace]))

(defprotocol Blank
  "Determines if a value is blank."
  (blank? [val]))

(extend-protocol Blank
  nil
  (blank? [_] true)
  java.lang.Object
  (blank? [_] false)
  java.lang.String
  (blank? [s] (str/blank? s))
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

(defn regexp?
  "Returns true if `r` is a regular expression"
  [r]
  (instance? java.util.regex.Pattern r))

(defn replace
  "Like clojure.string/replace but it also works with keywords

   sk: a string or a keyword
   match: a regexp expression
   replacement: what to replace for"
  [sk match replacement]
  (let [result (-> sk name (str/replace match replacement))]
    (if (keyword? sk)
      (keyword result)
      result)))

(defn underscore
  "Returns a dasherized string or keyword into an underscored one"
  [sk]
  (replace sk #"-" "_"))

(defn dasherize
  "Returns a underscored string or keyword into an dasherized one"
  [sk]
  (replace sk #"_" "-"))

(defn map-keys
  "Maps a function to the keys of a map"
  [f m]
  (reduce-kv (fn [nm k v] (assoc nm (apply f [k]) v)) {} m))

(s/fdef blank?
  :args (s/cat :val any?)
  :ret boolean?)

(s/fdef email?
  :args (s/cat :s (s/or :string string? :nil nil?))
  :ret boolean?)

(s/fdef regexp?
  :args(s/cat :val any?)
  :ret boolean?)

(s/fdef replace
  :args (s/cat :sk (s/or :keyword keyword? :string string?)
               :match regexp?
               :replacement string?)
  :ret (s/or :keyword keyword? :string string?))

(s/fdef underscore
  :args (s/cat :sk (s/or :keyword keyword? :string string?))
  :ret (s/or :keyword keyword? :string string?))

(s/fdef dasherize
  :args (s/cat :sk (s/or :keyword keyword? :string string?))
  :ret (s/or :keyword keyword? :string string?))

(s/fdef map-keys
  :args (s/cat :f fn? :m map?)
  :ret map?)
