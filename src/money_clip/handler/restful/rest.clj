(ns money-clip.handler.restful.rest
  (:require [clojure.spec.alpha :as s]
            [money-clip.utils :as ut]
            [uritemplate-clj.core :as uri]))

(s/def ::name keyword?)
(s/def ::include (s/map-of keyword? vector?))
(s/def ::exclude (s/coll-of (s/or :keyword keyword? :vector (s/coll-of keyword? :kind vector?)) :kind vector? :distinct true :into #{}))
(s/def ::links (s/map-of keyword? string?))

(defn- process-inclusions
  [resource inclusions]
  (letfn [(includer [resource attr path]
            (assoc resource attr (get-in resource path)))]
    (reduce-kv includer resource inclusions)))

(defn- process-links
  [resource links]
  (letfn [(linker [resource attr uri-template]
            (assoc-in resource [:_links attr] (uri/uritemplate uri-template resource)))]
    (reduce-kv linker resource links)))

(defn- process-exclusions
  [resource exclusions]
  (letfn [(excluder [resource attr]
            (ut/dissoc-in resource attr))]
    (reduce excluder resource (map ut/vectorize exclusions))))

(defn resource
  "Transforms a domain entity into a rest resource"
  [model name & {:keys [include exclude links] :or {include [] exclude [] links []}}]
  {:pre [(map? model) (keyword? name)]}
  (let [value (-> model
                  ut/unqualify-keys
                  (process-inclusions include)
                  (process-links links)
                  (process-exclusions exclude))]
    {name value}))

(defmacro defresource
  "Defines a REST resource"
  [name & decls]
  `(defn ~(symbol (str name "-resource"))
     [model#]
     (resource model# ~(keyword name) ~@decls)))

(s/fdef resource
  :args (s/cat
         :model map?
         :name ::name
         :kwargs (s/keys* :opt-un [::include ::exclude ::links]))
  :ret map?)
