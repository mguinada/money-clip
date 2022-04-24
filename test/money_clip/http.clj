(ns money-clip.http
  "A thin wrapper over periodt."
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [ring.core.spec]
            [peridot.core :as p]
            [muuntaja.core :as m]))

(defn request
  "Wraps peridot `session` and `request`.
   This is basically a convenience function tailred for restfull request with JSON payloads."
  [method app uri body & {:keys [headers auth-token] :or {headers {} auth-token ""}}]
  {:pre (map? headers)}
  (let [m (m/create)
        content-type "application/json"
        add-headers (partial reduce-kv (fn [app header val] (p/header app header val)))]
    (if-not (empty? auth-token) (assoc headers "Authorization" (str "Token " auth-token)) nil)
    (-> (p/session app)
        (add-headers headers)
        (p/request uri
                   :request-method method
                   :content-type content-type
                   :body (->> body
                              (m/encode m content-type)
                              (slurp))))))

(defn response
  "Returns the decoded response"
  [request]
  (:response request))

(defn status
  "Returns the response status"
  [request]
  (-> request
      response
      :status))

(defn body
  "Returns the response body.
   Optionaly a list of keys can be proivided to project content from inside the body
   "
  [request & keys]
  (let [body (-> request response m/decode-response-body)]
    (if-not (empty? keys)
      (get-in body keys)
      body)))

(s/def ::headers map?)
(s/def ::methods #{:get :post :put :delete})


(s/fdef request
  :args (s/cat
         :method ::methods
         :app :ring/handler
         :uri string?
         :body map?
         :kwargs (s/keys* :opt-un [::headers ::auth-token]))
  :ret :ring/response)

(defn- generate-verbs
  "Generates functions that call `request` by using an all caps HTTP verb
   as the symbol which translates to the HTTP method use to request.
   
   example:
   
   (POST @db/app \"/tasks\" {:description \"Do more clojure!\"})
   "
  []
  (doseq [method (s/describe ::methods)]
    (eval
     `(def ~(symbol (str/upper-case (name method)))
        (partial request ~method)))))

(generate-verbs)
