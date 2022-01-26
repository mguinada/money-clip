(ns money-clip.utils-test
  (:require [clojure.test :refer [deftest testing is]]
            [money-clip.utils :as u]))

(deftest blank?-test
  (testing "blank values"
    (is (u/blank? nil) "`nil` is blank")
    (is (u/blank? "") "an empty string is blank")
    (is (u/blank? []) "an empty collection is blank")
    (is (u/blank? {}) "an empty map is blank")))

(deftest not-blank?-test
  (testing "non blank values"
    (is (u/not-blank? 1) "a number is not blank")
    (is (u/not-blank? "a string") "a non empty string is not blank")
    (is (u/not-blank? [""]) "an collection with elements is not blank")
    (is (u/not-blank? {:a 1}) "a map with elements is not blank")
    (is (u/not-blank? (java.util.Date.)) "an object is not blank")))

(deftest email?-test
  (testing "invalid email format"
    (is (false? (u/email? nil)))
    (is (false? (u/email? "")))
    (is (false? (u/email? "@")))
    (is (false? (u/email? "abc@")))
    (is (false? (u/email? "abc@def")))
    (is (false? (u/email? "abc@def_com"))))
  (testing "valid email format"
    (is (true? (u/email? "abc@def.org")))
    (is (true? (u/email? "abc@d.e.f.org")))
    (is (true? (u/email? "abc+ghz@def.org")))))
