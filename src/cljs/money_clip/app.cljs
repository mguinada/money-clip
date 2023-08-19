(ns money-clip.app
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame]
            [money-clip.events :as events]
            [money-clip.subs :as subs]
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

(defn splash []
  (when @(re-frame/subscribe [::subs/session-loading?])
    [:div#splash-container.columns
     [:div.column.is-full.has-text-centered
      [:h1 "Welcome to money|clip!"]
      [:div.center {:style {:width "5em"}}
       [:progress.progress.is-dark.is-small {:max 100} "30%"]]]]))

(defn app []
  [:div
   [nav/navigation]
   [:section {:class "section is-main-section"}
    [:noscript "money|clip is a JavaScript app. Please enable JavaScript to continue."]
    [splash]
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
