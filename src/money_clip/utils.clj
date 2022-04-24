(ns money-clip.utils
  "A collection of convenience functions"
  (:require [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
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

(defn throwable?
  "Returns true if `t` is a Throwable"
  [t]
  (instance? java.lang.Throwable t))

(defn exception-info?
  "Returns true if `t` is a clojure.lang.ExceptionInfo"
  [t]
  (instance? clojure.lang.ExceptionInfo t))

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

(defn qkey
  "Returns a qualified keyword namespaced to `ns`.
   If the given keyword is already namespaced, it will be \"requalified\" to the given namespace."
  [k ns]
  (keyword (name ns) (name k)))

(defn unqkey
  "turns a qualified keyword into unqualified"
  [k]
  (-> k name keyword))

(defn nested-map?
  "Returns true if `m` is a nested map"
  [m]
  {:pre [(map? m)]}
  (boolean (some map? (map (fn [[_ v]] v) m))))

(defn map-keys
  "Maps a function to the keys of a map"
  [f m]
  {:pre [(fn? f) (map? m)]}
  (reduce-kv (fn [nm k v] (assoc nm (apply f [k]) v)) {} m))

(defn map-values
  "Maps a function to the keys of a map"
  [f m]
  {:pre [(fn? f) (map? m)]}
  (reduce-kv (fn [nm k v] (assoc nm k (apply f [v]))) {} m))

(defn transform-key-values
  "Applies a tranformation by aplying `f` on `m` key value pairs

   The `f` tranformer function receives the a vector with the key and the value as 1st and 2nd elements
   and must return also a vector with the key as it's 1st element and the value as it 2nd.

   ;; Ex.: keywordize a deep nested map:
   (transform-key-values (fn [[k v]] [(keyword? k) v]) a-map)"
  [f m]
  {:pre [(fn? f) (map? m)]}
  (walk/postwalk (fn [m] (if (map? m) (into {} (map f m)) m)) m))

(defn qualify-keys
  "Turns a map's key into qualified keys.
   If some keys are already namespaced, it will be \"requalified\" to the given namespace."
  [m ns]
  (if-not (nil? m)
    (transform-key-values (fn [[k v]] [(qkey k ns) v]) m)
    nil))

(defn unqualify-keys
  "Turns a map's key into unqualified keys."
  [m]
  (if-not (nil? m)
    (transform-key-values (fn [[k v]] [(unqkey k) v]) m)
    nil))

(defn vectorize
  "Wraps val in a vector"
  [val]
  (cond
    (vector? val) val
    (coll? val) (vec val)
    :else (vector val)))

(defn dissoc-in
  "Dissociates a value in a nested associative structure"
  [m [k & ks]]
  (if ks
    (update-in m (cons k (butlast ks)) dissoc (last ks))
    (dissoc m k)))

(defn sort-map-keys
  "Sort a map by key taking `key-order` as the ordering criterion.
   Keys that are present on the map but not in the ordering vector 
   will be at the tail of the map.
   "
  [m key-order]
  {:pre [(map? m) (vector? key-order)]}
  (let [key-indexes (-> key-order (concat (vec (keys m))) distinct (zipmap (range)))
        sorter (fn [x y] (< (get key-indexes x) (get key-indexes y)))]
    (into (sorted-map-by sorter) m)))

(s/fdef blank?
  :args (s/cat :val any?)
  :ret boolean?)

(s/fdef email?
  :args (s/cat :s (s/or :string string? :nil nil?))
  :ret boolean?)

(s/fdef regexp?
  :args(s/cat :val any?)
  :ret boolean?)

(s/fdef throwable?
  :args (s/cat :t any?)
  :ret boolean?)

(s/fdef exception-info?
  :args (s/cat :t any?)
  :ret boolean?)

(s/fdef replace
  :args (s/cat
         :sk (s/or :keyword keyword? :string string?)
         :match regexp?
         :replacement string?)
  :ret (s/or :keyword keyword? :string string?))

(s/fdef underscore
  :args (s/cat :sk (s/or :keyword keyword? :string string?))
  :ret (s/or :keyword keyword? :string string?))

(s/fdef dasherize
  :args (s/cat :sk (s/or :keyword keyword? :string string?))
  :ret (s/or :keyword keyword? :string string?))

(s/fdef qkey
  :args (s/cat :keyword (s/or :keyword keyword? :string string?) :ns (s/or :symbol symbol? :string string?))
  :ret qualified-keyword?)

(s/fdef unqkey
  :args (s/cat :keyword keyword?)
  :ret keyword?)

(s/fdef nested-map?
  :args (s/cat :map map?)
  :ret boolean?)

(s/fdef map-keys
  :args (s/cat :f fn? :m map?)
  :ret map?)

(s/fdef map-values
  :args (s/cat :f fn? :m map?)
  :ret map?)

(s/fdef transform-key-values
  :args (s/cat :f fn? :m map?)
  :ret map?)

(s/fdef qualify-keys
  :args (s/cat :map (s/or :map map? :nil nil?) :ns (s/or :symbol symbol? :string string?))
  :ret (s/or :map map? :nil nil?))

(s/fdef unqualify-keys
  :args (s/cat :map (s/or :map map? :nil nil?))
  :ret (s/or :map map? :nil nil?))

(s/fdef vectorize
  :args (s/cat :val any?)
  :ret vector?)

(s/fdef dissoc-in
  :args (s/cat :m map? :v vector?)
  :ret map?)

(s/fdef sort-map-keys
  :args (s/cat :m map? :key-order vector?)
  :ret map?)
