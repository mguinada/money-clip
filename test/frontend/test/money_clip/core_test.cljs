(ns money-clip.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [money-clip.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
