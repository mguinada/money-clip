(ns money-clip.routes
  (:require [reitit.core :as r]
            [reitit.coercion :as rc]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [reitit.coercion.spec :as rss]
            [reitit.frontend :as rf]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as rfe]
            [re-frame.core :as re-frame]
            [money-clip.views.home :as home]))

(re-frame/reg-event-db
 ::navigated
 (fn-traced [db [_ new-match]]
   (let [old-match   (:routes/current db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :routes/current (assoc new-match :controllers controllers)))))

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
