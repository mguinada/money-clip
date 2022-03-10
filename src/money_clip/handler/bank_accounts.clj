(ns money-clip.handler.bank-accounts
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [args]
    (println "args " args)
    [::response/ok "Hello World"]))
