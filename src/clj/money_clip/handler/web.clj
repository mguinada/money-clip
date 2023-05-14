(ns money-clip.handler.web
  (:require  [compojure.core :refer [GET]]
             [clojure.java.io :as io]
             [integrant.core :as ig]))

(defmethod ig/init-key :money-clip.handler.web/root [_ _]
  (GET "/" []
    (io/resource "money_clip/public/index.html")))

(defmethod ig/init-key :money-clip.handler.web/site [_ _]
  (GET "/:path" [_]
    (io/resource "money_clip/public/index.html")))
