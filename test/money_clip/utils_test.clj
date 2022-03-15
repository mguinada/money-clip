(ns money-clip.utils-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [money-clip.utils :as ut]))

(st/instrument)

(deftest blank?-test
  (testing "blank values"
    (is (ut/blank? nil) "`nil` is blank")
    (is (ut/blank? "") "an empty string is blank")
    (is (ut/blank? []) "an empty collection is blank")
    (is (ut/blank? {}) "an empty map is blank")))

(deftest not-blank?-test
  (testing "non blank values"
    (is (ut/not-blank? 1) "a number is not blank")
    (is (ut/not-blank? "a string") "a non empty string is not blank")
    (is (ut/not-blank? [""]) "an collection with elements is not blank")
    (is (ut/not-blank? {:a 1}) "a map with elements is not blank")
    (is (ut/not-blank? (java.util.Date.)) "an object is not blank")))

(deftest email?-test
  (testing "invalid email format"
    (is (false? (ut/email? nil)))
    (is (false? (ut/email? "")))
    (is (false? (ut/email? "@")))
    (is (false? (ut/email? "abc@")))
    (is (false? (ut/email? "abc@def")))
    (is (false? (ut/email? "abc@def_com"))))
  (testing "valid email format"
    (is (true? (ut/email? "abc@def.org")))
    (is (true? (ut/email? "abc@d.e.f.org")))
    (is (true? (ut/email? "abc+ghz@def.org")))))

(deftest regexp?-test
  (testing "is a regexp"
    (is (true? (ut/regexp? #""))))
  (testing "is not a regexp"
    (is (false? (ut/regexp? "")))))

(deftest throwable?-test
  (testing "when it's an exception"
    (let [error (java.lang.Error.)]
      (is (true? (ut/throwable? error)))))
  (testing "when it's not an exception"
    (is (false? (ut/throwable? (java.lang.Object.))))))

(deftest exception-info?-test
  (testing "when it's an instance of `clojure.lang.ExceptionInfo`"
    (let [error (clojure.lang.ExceptionInfo. "" {})]
      (is (true? (ut/exception-info? error)))))
  (testing "when it's an instance of `clojure.lang.ExceptionInfo`"
    (is (false? (ut/exception-info? (java.lang.Object.))))))

(deftest replace-test
  (testing "relaces a matched pattern in a keyword"
    (is (= :last-name (ut/replace :surename #"sure" "last-")))
  (testing "relaces a matched pattern in a string"
    (is (= "last-name" (ut/replace "surename" #"sure" "last-"))))))

(deftest underscore-test
  (testing "turns a dasherized keyword into an underscored one"
    (is (= :billing_address_city (ut/underscore :billing-address-city))))
  (testing "turns a dasherized tring into an underscored one"
    (is (= "billing_address_city" (ut/underscore "billing-address-city")))))

(deftest dasherize-test
  (testing "turns a underscored keyword into an dasherized one"
    (is (= :billing-address-city (ut/dasherize :billing_address_city))))
  (testing "turns a underscored string into an dasherized one"
    (is (= "billing-address-city" (ut/dasherize "billing_address_city")))))

(deftest map-keys-test
  (testing "maps a function to the keys of a map"
    (is (= {"a" 1 "b" 2 "c" 3} (ut/map-keys #(name %) {:a 1 :b 2 :c 3})))))

(deftest qkey
  (testing "builds a qualified keyword"
    (is (= ::keyword (ut/qkey :keyword 'money-clip.utils-test)))
    (is (= ::keyword (ut/qkey :already.namespaced/keyword 'money-clip.utils-test)))
    (is (= ::keyword (ut/qkey "keyword" 'money-clip.utils-test)))
    (is (= ::keyword (ut/qkey "keyword" "money-clip.utils-test")))))

(deftest unqkey
  (testing "turns a qualified keyword into unqualified"
    (is (= :keyword (ut/unqkey ::keyword)))))

(deftest qualify-keys
  (testing "qualifies the key of a map"
    (is (= {::a 1 ::b 2 ::c 3 ::d 4} (ut/qualify-keys {:a 1 :b 2 :c 3 :d 4} 'money-clip.utils-test))))
  (testing "when given `nil` as a map"
    (is (nil? (ut/qualify-keys nil 'money-clip.utils-test)))))

(deftest unqualify-keys
  (testing "unqualifies the key of a map"
    (is (= {:a 1 :b 2 :c 3 :d 4} (ut/unqualify-keys {::a 1 ::b 2 ::c 3 ::d 4}))))
  (testing "when given `nil` as a map"
    (is (nil? (ut/unqualify-keys nil)))))
