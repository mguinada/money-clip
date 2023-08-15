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
       (assoc :routes/current nil :session/loading? true))))

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
 (fn-traced [db [_ user]]
   (assoc db :user user :session/loading? false)))

(re-frame/reg-event-fx
 ::login-success
 (fn-traced [db [_ {:keys [user]}]]
   {:dispatch-n [[::set-user _ user] [::navigate :home]]}))

(re-frame/reg-event-db
 ::login-failure
 (fn-traced [db [_ {:keys [response]}]]
   (assoc db :login-error response)))

(re-frame/reg-event-fx
 ::login
 (fn-traced [_ [_ email password]]
   {:http-xhrio {:method :post
                 :uri "/api/login"
                 :params {:email email :password password}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::login-success]
                 :on-failure [::login-failure]}}))

(re-frame/reg-event-fx
 ::load-session
 (fn-traced [_ _]
   {:http-xhrio {:method :get
                 :uri "api/session"
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-session]
                 :on-failure [::no-session]}}))

(re-frame/reg-event-fx
 ::set-session
 (fn-traced [_ [_ {jwt :token :as args}]]
   {:http-xhrio {:method :get
                 :uri "api/user"
                 :headers {:accept "application/json" :authorization (str "Token" " " jwt)}
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::session-init]
                 :on-failure [::no-session]}}))

(re-frame/reg-event-fx
 ::session-init
 (fn-traced [{:keys [db]} [_ {:keys [user]}]]
   {:dispatch-n [[::set-user _ user] [::post-session-redirect]]}))

(re-frame/reg-event-fx
 ::no-session
 (fn-traced [{:keys [db]} _]
   {:db (assoc db :session/loading? false)
    :dispatch [::navigate :sign-in]}))

(re-frame/reg-event-fx
 ::post-session-redirect
 (fn-traced [{:keys [db]} _]
   {:dispatch [::navigate (get-in db [:routes/requested :data :name])]}))
