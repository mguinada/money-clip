(ns money-clip.http
  "A thin wrapper over periodt."
  (:require [clojure.string :as str]
            [peridot.core :as p]
            [muuntaja.core :as m]))

(defn request
  "Wraps peridot `session` and `request`.
   This is basicallty a convenience function tailred for restfull request with JSON payloads."
  [app method uri body]
  (let [m (m/create) 
        content-type "application/json"]
    (-> (p/session app)
        (p/request uri
                   :request-method method
                   :content-type content-type
                   :body (->> body
                              (m/encode m content-type)
                              (slurp))))))

(defn response
  "Returns the decoded response"
  [request]
  (->> request :response))

(defn status
  "Returns the response status"
  [request]
  (-> request
      response
      :status))

(defn body
  "Returns the response body"
  [request & keys]
  (let [body (-> request response m/decode-response-body)]
    (if-not (empty? keys)
      (get-in body keys)
      body)))

(defn- generate-verbs
  "Generates functions that call `request` by using an all caps HTTP verb
   as the symbol which translates to the HTTP method use to request.
   
   example:
   
   (POST @db/app \"/tasks\" {:description \"Do more clojure!\"})
   "
  []
  (doseq [verb [:get :put :post :delete]]
    (eval
     `(def ~(symbol (str/upper-case (name verb)))
        (fn [app# uri# body#]
          (request app# ~verb uri# body#))))))

(generate-verbs)
