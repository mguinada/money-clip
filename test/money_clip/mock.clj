(ns money-clip.mock
  (:require [clojure.spec.alpha :as s]
            [ring.mock.request :as rm])
  (:refer-clojure :exclude [identity]))

(defn request
  "Decorates a ring mock request with specifics of a duct request"
  ([method uri]
   (request method uri nil))
  ([method uri params]
   (-> (rm/request method uri params)
       (assoc :body-params params 
              :ataraxy/result (->> params vals (cons nil) vec)))))

(defn identity
  [request id]
  (-> request
      (assoc :identity id)))

(s/fdef request
  :args (s/cat
         :method #{:get :post :put :head :delete}
         :uri string?
         :params (s/or :map map :nil nil?))
  :ret map?)

(s/fdef identity
  :args (s/cat
         :request map?
         :id map?)
  :ret map?)
