(ns money-clip.app-test
  (:require-macros [latte.core :refer (before describe it)])
  (:require [latte.chai :refer (expect)])
  (:refer-clojure :exclude [first get]))

(describe "Heart beat"
  (before [])
  (it "it's alive" []
    (expect 1 :not.to.equal 2)))
