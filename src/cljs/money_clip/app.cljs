(ns money-clip.app
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [money-clip.events :as events]
            [money-clip.routes :as routes]
            [money-clip.views.navigation :as nav]
            [money-clip.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn router-component [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::routes/current-route])]
    [:div
     (when current-route
       [(do 
          (-> current-route :data :view))])]))

(defn app []
  [:div
   [nav/navigation]
   [:section {:class "section is-main-section"}
    [:noscript "money|clip is a JavaScript app. Please enable JavaScript to continue."]
    [router-component {:router routes/router}]]])

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (routes/start!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [app] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/initialize-app])
  (dev-setup)
  (mount-root))
