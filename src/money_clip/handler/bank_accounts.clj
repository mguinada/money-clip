(ns money-clip.handler.bank-accounts
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]
            [buddy.auth :as auth]))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{:keys [identity] :as request}]
    ;; (println "args" args)
    (println "REQUEST" request)
    (println "ID" identity)
    (println "AUTHENTICATED?" (auth/authenticated? request))
    (println)
    ;; (when-not (auth/authenticated? request) (auth/throw-unauthorized))
    [::response/ok "Hello World"]))
