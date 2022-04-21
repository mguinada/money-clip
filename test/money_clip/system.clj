(ns money-clip.system
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [duct.core :as duct]
            [clojure.java.jdbc :as jdbc]
            [money-clip.http :as http :refer [POST]]))

(defn- read-config
  []
  (duct/read-config (io/resource "money_clip/config.edn")))

(defn- init-system
  []
  (duct/load-hierarchy)
  (-> (read-config)
      duct/prep-config
      (ig/init [:duct.profile/test :duct.handler/root :duct.database/sql :duct.migrator/ragtime])))

(defn- truncate
  [{db :spec} & tables]
  (let [tables (if (empty? tables)
                 (map :relname (jdbc/query db ["SELECT relname FROM pg_stat_user_tables WHERE relname <> 'ragtime_migrations'"]))
                 (map name tables))]
    (doseq [table tables]
      (jdbc/execute! db [(str "TRUNCATE TABLE " table " CASCADE")]))))

(def db (atom nil))
(def app (atom nil))

(defn init
  "Initializes the system so that we can interact with the test database.
   Built based on snippets from:
   - https://github.com/duct-framework/duct/issues/60
   - https://clojurians.slack.com/archives/C5K1SHR6X/p1597695483350500"
  [test-fn]
  (let [system (init-system)]
    (reset! app (:duct.handler/root system))
    (reset! db (:duct.database.sql/hikaricp system))
    (test-fn)
    (reset! db nil)
    (reset! app nil)
    (ig/halt! system)))

(defn cleanup
  "Test fixture that truncates the database.
   It is to be used with the init fixture as it needs a db connnection.

   ```
   (t/use-fixtures :once db/init)
   (t/use-fixtures :each db/cleanup)
   ```

   *NOTICE*: Test runners that run test in parallel can break the truncation step.

   e.g. if you are using https://github.com/weavejester/eftest you must disable parallelism
   with `(run-tests (find-tests \"test\") {:multithread? false})`.
   "
  [test-fn]
  (truncate @db)
  (test-fn))

(defn create-user-and-login
  ([app]
   (create-user-and-login app "test@users.net"))
  ([app email]
   (create-user-and-login app email "Test" "User"))
  ([app email first-name last-name]
   (let [password "pa66word"
         create-user-response (POST app "/users" {:email email
                                                  :password password
                                                  :password-confirmation password
                                                  :first-name first-name
                                                  :last-name last-name})
         login-user-response (POST app "/login" {:email email :password "pa66word"})]
     (assert (= 201 (http/status create-user-response)) "Failed to create user")
     (assert (= 200 (http/status login-user-response)) "Failed to login")
     [(http/body create-user-response :user) (http/body login-user-response :user :auth-token) password])))
