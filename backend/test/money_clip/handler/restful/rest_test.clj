(ns money-clip.handler.restful.rest-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.spec.test.alpha :as st]
            [money-clip.handler.restful.rest :as rest]))

(st/instrument)

(deftest resource-test
  (let [model {:model.bank-account/id 1,
               :model.bank-account/user {:model.user/id 2,
                                         :model.user/email "john.doe@doe.net",
                                         :model.user/first-name "John",
                                         :model.user/last-name "Doe"},
               :model.bank-account/name "Savings",
               :model.bank-account/balance {:amount 1000
                                            :currency :EUR
                                            :exchange-rate-date (java.util.Date.)}
               :model.bank-account/bank-name "IBANK"}]
    (testing "turns a model into a rest resource"
      (is (= {:bank_account {:id 1
                             :name "Savings"
                             :user_email "john.doe@doe.net",
                             :balance {:amount 1000 :currency :EUR}
                             :_links {:self "/bank-accounts/1"
                                      :user "/users/2"
                                      :movements "/bank-accounts/1/movements"}}}
             (rest/resource model :bank-account
                            :include {:user_id [:user :id]
                                      :user-email [:user :email]}
                            :exclude [:user :bank-name :user_id [:balance :exchange-rate-date]]
                            :links {:self "/bank-accounts/{id}"
                                    :user "/users/{user_id}"
                                    :movements "/bank-accounts/{id}/movements"}))))
    (testing "when attribute order is not specified"
      (let [resource (rest/resource model :bank-account
                                    :include {:user_id [:user :id]
                                              :user-email [:user :email]}
                                    :exclude [:user :bank-name :user_id [:balance :exchange-rate-date]]
                                    :links {:self "/bank-accounts/{id}"
                                            :user "/users/{user_id}"
                                            :movements "/bank-accounts/{id}/movements"})]
        (is (= [:id :name :balance :user_email :_links] (-> resource :bank_account keys vec))) "keeps original key order and adds new keys to the tail of the map"))
    (testing "when attribute order is specified"
      (let [resource (rest/resource model :bank-account
                                    :include {:user_id [:user :id]
                                              :user-email [:user :email]}
                                    :exclude [:user :bank-name :user_id [:balance :exchange-rate-date]]
                                    :links {:self "/bank-accounts/{id}"
                                            :user "/users/{user_id}"
                                            :movements "/bank-accounts/{id}/movements"}
                                    :attr-order [:id :balance :user-email :name :_links])]
        (is (= [:id :balance :user_email :name :_links] (-> resource :bank_account keys vec))) "complies with the specified order"))))

(deftest defresource-test
  (let [model {:model.bank-account/id 1,
               :model.bank-account/user {:model.user/id 2,
                                         :model.user/email "john.doe@doe.net",
                                         :model.user/first-name "John",
                                         :model.user/last-name "Doe"},
               :model.bank-account/name "Savings",
               :model.bank-account/balance {:amount 1000
                                            :currency :EUR
                                            :exchange-rate-date (java.util.Date.)}
               :model.bank-account/bank-name "IBANK"}]
    (rest/defresource bank-account
      :include {:user_id [:user :id] :user-email [:user :email]}
      :exclude [:user :bank-name :user_id [:balance :exchange-rate-date]]
      :links {:self "/bank-accounts/{id}" :user "/users/{user_id}" :movements "/bank-accounts/{id}/movements"})
    (is (= {:bank_account {:id 1
                           :name "Savings"
                           :user_email "john.doe@doe.net",
                           :balance {:amount 1000 :currency :EUR}
                           :_links {:self "/bank-accounts/1"
                                    :user "/users/2"
                                    :movements "/bank-accounts/1/movements"}}}
           (bank-account-resource model)))))
