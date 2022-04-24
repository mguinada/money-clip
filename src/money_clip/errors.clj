(ns money-clip.errors
  (:require [clojure.spec.alpha :as s]
            [money-clip.utils :as ut]))

(s/def ::type qualified-keyword?)
(s/def ::reason qualified-keyword?)
(s/def ::attribute keyword?)
(s/def ::value (s/or :string string? :number number?))
(s/def ::data (s/keys :req-un [::attribute] :opt [::value]))
(s/def ::ex-info (s/keys :req-un [::type ::reason] :opt [::data]))

(defn- error
  "Creates structured error information via `ex-info`.
   
   example:

   (e/error :uniqueness-violation \"Email is taken\" ::email-taken {:attribute :email :value \"jdoe@doe.net\"}                                                                                                         
   "
  ([type respondable msg reason]
   (error type respondable msg reason nil))
  ([type respondable msg reason data]
   (when respondable (derive type ::respondable))
   (ex-info
    msg
    {:type type
     :reason reason
     :data data})))

(defn respondable?
  "Determins if the error is of a kind that the API should react 
   to by serving a strcutured response or if it should let it bubble up the stack.
   "
  [error]
  (-> error
      ex-data
      :type
      (isa? ::respondable)))

(defn contains-data?
  [error]
  (-> error ex-data :data nil? not))

(def ^:const unauthorized {:error {:message "Unauthorized"}})
(def ^:const permission-denied {:error {:message "Permission denied"}})

(def uniqueness-violation-error (partial error ::uniqueness-violation-error true))
(def passwords-dont-match-error (partial error ::passwords-dont-match-error true))
(def invalid-password (partial error ::invalid-password-error true))
(def fatal-error (partial error ::fatal-error false))

(defn ex-response
  "Translates an exception to a map with the error info"
  [e]
  (let [response {:error {:message (ex-message e)}}]
    (if (contains-data? e)
      (assoc-in response [:error :data] (:data (ex-data e)))
      response)))

(defmacro try-catch
  "A macro the wraps `body` in a try/catch for the `clojure.lang.ExceptionInfo` exception class.
   If an respondale exception is caught, it will be turned into the corresponding map,
   otherwise the exception will be rethrown."
  [& body]
  `(try
     ~@body
     (catch clojure.lang.ExceptionInfo e#
       (if (e/respondable? e#)
         {:status 412 :headers {} :body (e/ex-response e#)}
         (throw e#)))))

(s/fdef error
  :args (s/cat
         :type ::type
         :respondable boolean?
         :msg string?
         :reason ::reason
         :data (s/or :data ::data :nil nil?))
  :ret ::ex-info)

(s/fdef respondable?
  :args (s/cat :error ut/exception-info?)
  :ret boolean?)

(s/fdef contains-data?
  :args (s/cat :error ut/exception-info?)
  :ret boolean?)

(s/fdef ex-response
  :args (s/cat :exception ut/exception-info?)
  :ret (s/keys :req-un [::error]))
