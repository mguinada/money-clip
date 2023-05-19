(ns money-clip.app
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [money-clip.events :as events]
            [money-clip.routes :as routes]
            [money-clip.views.home :refer [main-panel]]
            [money-clip.views.navigation :refer [navbar]]
            [money-clip.config :as config]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn app []
  [:div.min-h-full
   [navbar]
   [:header {:class "bg-white shadow"}
    [:div {:class "mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8"}
     [:h1 {:class "text-3xl font-bold tracking-tight text-gray-900"} "Dashboard"]]]
   [:main
    [:div {:class "mx-auto max-w-7xl py-6 sm:px-6 lg:px-8"}
     [:noscript "money|clip is a JavaScript app. Please enable JavaScript to continue."]
     [main-panel]]]])

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [app] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch [::events/initialize-app])
  (dev-setup)
  (mount-root))
