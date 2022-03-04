(ns money-clip.middleware.unqualifier
  (:require [integrant.core :as ig]
            [money-clip.utils :as ut]))

(defn unqualify
  [handler]
  (fn [request]
    (println "REQUEST" request)
    (handler request))
  (fn [request response raise]
    (println "RESPONSE" response)
    (handler request response raise)))

(defmethod ig/init-key ::unqualify [_ opts]
  #(unqualify %))
