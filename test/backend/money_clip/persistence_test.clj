(ns money-clip.persistence-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.spec.test.alpha :as st]
   [money-clip.model.user :as u]
   [money-clip.persistence :as p]))

(st/instrument)

(deftest serializer-test
  (testing "creates a model serializer"
    (let [timestamp (java.util.Date.)
          data [{:id 1 :email "john.doe@deonet.org" :password "pa66w0rd" :first_name "John" :last_name "Doe" :active false :created_at timestamp :updated_at timestamp}]
          serializer (p/serializer u/user :id :email :first_name :last_name :active :created_at :updated_at)]
      (is (= (apply u/user (-> data first (dissoc :password) vals)) (serializer data))))))
