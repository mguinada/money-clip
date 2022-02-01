(ns money-clip.handler.users
  (:require [ataraxy.response :as response]
            [buddy.hashers :as hs]
            [clojure.java.jdbc :as jdbc]
            [integrant.core :as ig]
            [duct.database.sql]))

(defprotocol Users
  (create-user [db email password first-name last-name]))

(extend-protocol Users
  duct.database.sql.Boundary
  (create-user [{db :spec} email password first-name last-name]
    (let [pwhash (hs/derive password)
          results (jdbc/insert! db :users {:email email :password pwhash :first_name first-name :last_name last-name :active true})]
      (-> results ffirst val))))

(defmethod ig/init-key ::create [_ {:keys [db]}]
  (fn [{[_ email password first-name last-name] :ataraxy/result}]
    (let [id (create-user db email password first-name last-name)]
      [::response/created (str "/users/" id)])))
