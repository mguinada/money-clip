(ns money-clip.routes
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [reitit.core :as r]
            [reitit.coercion :as rc]
            [reitit.coercion.spec :as rss]
            [reitit.frontend :as rf]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as rfe]
            [money-clip.views.home :as home]))

(def session-fencing
   "Enforces session fencing. i.e. it prevents an unauthenticated user to access
    private screens and authenticated users from accessing pages that are exclusive
    for unauthenticated users. e.g. the sign-in screen"
  (re-frame/->interceptor
   :id ::session-fencing
   :before (fn [{{[_ {{route-name :name} :data}] :event :as event {user :user :as db} :db} :coeffects :as ctx}]
             (cond
               (:session/loading? db) ctx
               (and (some? user) (= :sign-in route-name)) (re-frame/dispatch [:money-clip.events/navigate :home])
               (and (nil? user) (not= :sign-in route-name)) (re-frame/dispatch [:money-clip.events/navigate :sign-in])
               :else ctx))
   :after (fn [{{db :db} :coeffects :as ctx}]
            (if (:session/loading? db)
              (assoc-in ctx [:effects :db :routes/current] nil)
              ctx))))

(re-frame/reg-event-db
 ::navigated
 [session-fencing]
 (fn-traced [db [_ new-match]]
   (let [old-match (:routes/current db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)
         target-route (assoc new-match :controllers controllers)]
     (-> db
         (assoc :routes/requested target-route)
         (assoc :routes/current target-route)))))

(re-frame/reg-sub
 ::current-route
 (fn-traced [db]
   (:routes/current db)))

(re-frame/reg-fx
 ::navigate!
 (fn-traced [route]
   (rfe/push-state route)))

(defn href
  "Return relative url for given route. Url can be used in HTML links."
  ([k]
   (href k nil nil))
  ([k params]
   (href k params nil))
  ([k params query]
   (rfe/href k params query)))

(def routes
  ["/"
   [""
    {:name :home
     :view home/home
     :link-text "Home"}]
   ["sign-in"
    {:name :sign-in
     :view home/sign-in
     :link-text "Sign in"}]
   ["about"
    {:name :about
     :view home/about
     :link-text "About"}]])

(defn on-navigate [new-match]
  (when new-match
    (re-frame/dispatch [::navigated new-match])))

(def router
  (rf/router
   routes
   {:data {:coercion rss/coercion}}))

(defn start! []
  (js/console.log "Initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
