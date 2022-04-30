(ns money-clip.handler.restful.rest
  (:require [clojure.spec.alpha :as s]
            [money-clip.utils :as ut]
            [uritemplate-clj.core :as uri]))

(s/def ::name keyword?)
(s/def ::include (s/map-of keyword? vector?))
(s/def ::exclude (s/coll-of (s/or :keyword keyword? :vector (s/coll-of keyword? :kind vector?)) :kind vector? :distinct true :into #{}))
(s/def ::links (s/map-of keyword? string?))
(s/def ::attr-order (s/coll-of keyword? :kind vector?))

(defn- process-inclusions
  [resource inclusions]
  (letfn [(includer [resource attr path] (assoc resource attr (get-in resource path)))]
    (reduce-kv includer resource inclusions)))

(defn- process-exclusions
  [resource exclusions]
  (letfn [(excluder [resource attr] (ut/dissoc-in resource attr))]
    (reduce excluder resource (map ut/vectorize exclusions))))

(defn- process-links
  [resource links]
  (letfn [(linker [resource attr uri-template]
            (assoc-in resource [:_links attr] (uri/uritemplate uri-template resource)))]
    (reduce-kv linker resource links)))

(defn resource
  "Transforms a domain entity into a rest resource.

   model: the domain model to be converted to a REST resource.
   name: the name of the resource. Must be a keyword and it will be the root key of the resource.
   include: attributes to include on the resource.
   exclude: attributes to be exlucuded of the resource.
   links: the HATEOAS links to be included on the resource."
  [model name & {:keys [include exclude links attr-order] :or {include [] exclude [] links [] attr-order []}}]
  {:pre [(map? model) (keyword? name)]}
  (let [attr-order (if (empty? attr-order) (-> model ut/unqualify-keys keys vec) attr-order)
        value (-> model
                  ut/unqualify-keys
                  (process-inclusions include)
                  (process-links links)
                  (process-exclusions exclude))]
    {name (ut/sort-map-keys value attr-order)}))

(defmacro defresource
  "Defines a REST resource.
   The name of function will be prefixed with resource.
   i.e. (defresource user) will bind to `user-resource`"
  [name & decls]
  `(defn ~(symbol (str name "-resource"))
     [model#]
     (resource model# ~(keyword name) ~@decls)))

(s/fdef resource
  :args (s/cat
         :model map?
         :name ::name
         :kwargs (s/keys* :opt-un [::include ::exclude ::links ::attr-order]))
  :ret map?)
