(ns money-clip.handler.web
  (:require  [compojure.core :refer [GET]]
             [clojure.java.io :as io]
             [integrant.core :as ig]))

(defmethod ig/init-key :money-clip.handler.web/index [_ _]
  (GET "/" []
    (io/resource "money_clip/public/index.html")))
