(ns money-clip.events
  (:require [re-frame.core :as re-frame]
            [money-clip.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   (-> db/default-db
       (assoc :routes/current nil))))

(re-frame/reg-event-fx
 ::initialize-app
 (fn-traced [_ _]
   {:dispatch [::load-session]}))

(re-frame/reg-event-fx
 ::navigate
 (fn [_ [_ route]]
   ;; See `navigate!` effect in routes.cljs
   {:money-clip.routes/navigate! route}))

(re-frame/reg-event-db
 ::set-user
 (fn-traced [db [_ {:keys [user]}]]
   (assoc db :user user)))

(re-frame/reg-event-db
 ::login-failure
 (fn-traced [db [_ {:keys [response]}]]
   (assoc db :login-error response)))

(re-frame/reg-event-fx
 ::login
 (fn-traced [{:keys [db]} [_ email password]]
   {:http-xhrio {:method :post
                 :uri "/api/login"
                 :params {:email email :password password}
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-session]
                 :on-failure [::login-failure]}}))

(re-frame/reg-event-fx
 ::load-session
 (fn-traced [{:keys [db]}]
   {:http-xhrio {:method :get
                 :uri "api/session"
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-session]}}))

(re-frame/reg-event-fx
 ::set-session
 (fn-traced [{:keys [db]} [_ {jwt :token}]]
            (if-not (nil? jwt)
              {:db (assoc db :jwt jwt)
               :http-xhrio {:method :get
                            :uri "api/user"
                            :headers {:authorization (str "Token" " " jwt)}
                            :timeout 5000
                            :format (ajax/json-request-format)
                            :response-format (ajax/json-response-format {:keywords? true})
                            :on-success [::set-user]}})))
