(ns money-clip.persistence.db
  (:require [clojure.java.io :as io]
            [integrant.core :as ig]
            [integrant.repl :as repl]
            [duct.core :as duct]
            [clojure.java.jdbc :as jdbc]))

(defn- read-config 
  []
  (duct/read-config (io/resource "money_clip/config.edn")))

(defn- init-system
  []
  (-> (read-config) 
      duct/prep-config 
      (ig/init [:duct.profile/test :duct.database/sql :duct.migrator/ragtime])))

(defn- truncate
  [{db :spec}]
  (let [tables (map :relname (jdbc/query db ["SELECT relname FROM pg_stat_user_tables WHERE relname <> 'ragtime_migrations'"]))]
    (doseq [table tables]
      (jdbc/execute! db [(str "TRUNCATE TABLE " table)]))))

(def db (atom nil))

(defn init
  "Initializes the system so that we can interact with the test database.
   Built based on snippets from:
   - https://github.com/duct-framework/duct/issues/60
   - https://clojurians.slack.com/archives/C5K1SHR6X/p1597695483350500"
  [test-fn]
  (let [system (init-system)]
    (reset! db (:duct.database.sql/hikaricp system))
    (test-fn)
    (reset! db nil)
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
   with `(eftest/run-tests (eftest/find-tests \"test\") {:multithread? :namespaces})` or
   even `(run-tests (find-tests \"test\") {:multithread? false})` depending on yout usage pattern.
   "
  [test-fn]
  (truncate @db)
  (test-fn))
