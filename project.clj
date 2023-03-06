(defproject money-clip "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.reader "1.3.6"]
                 [duct/core "0.8.0"]
                 [duct/module.ataraxy "0.3.0"]
                 [duct/module.logging "0.5.0"]
                 [duct/module.sql "0.6.1"]
                 [duct/module.web "0.7.3"]
                 [duct/middleware.buddy "0.2.0"]
                 [org.postgresql/postgresql "42.2.19"]
                 [buddy/buddy-hashers "1.8.158"]
                 [buddy/buddy-sign "3.4.333"]
                 [com.github.seancorfield/honeysql "2.2.868"]
                 [uritemplate-clj "1.3.1"]
                 [tick "0.5.0-RC5"]]
  :plugins [[duct/lein-duct "0.12.3"]
            [venantius/ultra "0.6.0"]]
  :main ^:skip-aot money-clip.main
  :uberjar-name  "money-clip-standalone.jar"
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev :profiles/instrument]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}
          :env {:lein-use-bootclasspath "no"}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :profiles/instrument {:injections [(require 'money-clip.utils)
                                      (require 'money-clip.handler.restful.rest)
                                      (require 'money-clip.errors)
                                      (require 'money-clip.model.user)
                                      (require 'money-clip.model.bank-account)
                                      (require 'money-clip.persistence)
                                      (require 'money-clip.persistence.users)
                                      (require 'money-clip.persistence.bank-accounts)
                                      (require 'money-clip.mock)
                                      (require 'clojure.spec.test.alpha)
                                      (clojure.spec.test.alpha/instrument)]}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [ring/ring-mock "0.4.0"]
                                   [ring/ring-spec "0.0.4"]
                                   [org.clojure/test.check "1.1.1"]
                                   [hawk "0.2.11"]
                                   [eftest "0.5.9"]
                                   [kerodon "0.9.1"]
                                   [com.gearswithingears/shrubbery "0.4.1"]
                                   [metosin/muuntaja "0.6.8"]
                                   [peridot "0.5.4"]]}})
