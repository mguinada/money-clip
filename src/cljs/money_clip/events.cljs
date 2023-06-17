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
 (fn-traced [db [_ {:keys [user]}]]
   (assoc db :user user :session/loading? false)))

(re-frame/reg-event-db
 ::login-failure
 (fn-traced [db [_ {:keys [response]}]]
   (assoc db :login-error response)))

(re-frame/reg-event-fx
 ::login
 (fn-traced [{:keys [db]} [_ email password]]
   (ajax/POST "/api/login"
     {:headers {"Accept" "application/json"}
      :params {:email email :password password}
      :format (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [response]
                 (re-frame/dispatch-sync [::set-user response])
                 (re-frame/dispatch [::navigate :home]))
      :error-handler (fn [response]
                        (re-frame/dispatch-sync [::login-failure response]))})))

(re-frame/reg-event-db
 ::post-session-redirect
 (fn-traced [db]
   (re-frame/dispatch [::navigate (get-in db [:routes/requested :data :name])])))

(re-frame/reg-event-fx
 ::load-session
 (fn-traced [{:keys [db]}]
   {:http-xhrio {:method :get
                 :uri "api/session"
                 :timeout 5000
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-session]
                 :on-failure [::no-session]}}))

(re-frame/reg-event-fx
 ::set-session
 (fn-traced [{:keys [db]} [_ {jwt :token :as args}]]
            (if (some? jwt)
              (ajax/GET "api/user"
                :headers {:accept "application/json" :authorization (str "Token" " " jwt)}
                :format (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :handler (fn [response]
                           (re-frame/dispatch-sync [::set-user response jwt])
                           (re-frame/dispatch-sync [::post-session-redirect])))
              (re-frame/dispatch [::no-session]))))


(re-frame/reg-event-fx
 ::no-session
 (fn-traced [{:keys [db]}]
   {:db (assoc db :session/loading? false)
    :dispatch [::navigate :sign-in]}))
