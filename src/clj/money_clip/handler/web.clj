(ns money-clip.handler.web
  (:require [ataraxy.response :as response]
            [clojure.java.io :as io]
            [integrant.core :as ig]))

(defmethod ig/init-key :money-clip.handler.web/index [_ _]
  (fn [_]
    [::response/ok (io/resource "money_clip/public/index.html")]))
