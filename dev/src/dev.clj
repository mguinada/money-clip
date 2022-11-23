(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [duct.core :as duct]
            [duct.core.repl :as duct-repl :refer [auto-reset]]
            [eftest.runner :as eftest]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]
            [clojure.java.jdbc :as jdbc]
            [money-clip.model.user :as u]
            [money-clip.persistence.users :as users]))

(duct/load-hierarchy)

(defn read-config []
  (duct/read-config (io/resource "money_clip/config.edn")))

(defn test
  ([]
   (test "test"))
  ([source]
   (eftest/run-tests (eftest/find-tests source) {:multithread? false})))

(def profiles
  [:duct.profile/dev :duct.profile/local])

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(when (io/resource "local.clj")
  (load "local"))

(integrant.repl/set-prep! #(duct/prep-config (read-config) profiles))

(defn db
  "Must call `go` first so that the system is initialized"
  []
  (-> system (ig/find-derived-1 :duct.database/sql) val :spec))

(defn q
  [sql]
  (jdbc/query (db) sql))

(defn e
  [sql]
  (jdbc/execute! (db) sql))

(defn create-user
  [email password first-name last-name]
  (-> system
      (ig/find-derived-1 :duct.database/sql)
      (last)
      (users/create-user (u/new-user email password first-name last-name) password)))
