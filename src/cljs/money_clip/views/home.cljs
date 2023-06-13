(ns money-clip.views.home
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [money-clip.events :as events]
            [money-clip.subs :as subs]))

(defn home []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [:div
      [:a {:on-click #(re-frame/dispatch [::events/navigate :about])} "go to About Page"]
      [:br]
      [:a {:on-click #(re-frame/dispatch [::events/navigate :sign-in])} "Sign-in"]]]))

(defn about []
  [:div
   [:h1 "This is the About Page."]
   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])} "go to Home Page"]]])

(defn sign-in []
  (let [fields (r/atom {})]
    (fn []
      [:div.sign-in.container.columns
       [:div.column.is-5.is-offset-2
        [:form.box
         {:on-submit (fn [e]
                       (.preventDefault e)
                       (re-frame/dispatch [::events/login (:email @fields) (:password @fields)]))}
         [:div.heading.has-text-centered
          [:h3.title "Login"]
          [:p.subtitle.is-size-7 "Please enter your email and password"]]
         [:div.field
          [:div.control
           [:input.input
            {:type "email"
             :placeholder "Email"
             :value (:email @fields)
             :on-change #(swap! fields assoc :email (-> % .-target .-value))}]]]
         [:div.field
          [:div.control
           [:input.input
            {:type "password"
             :placeholder "Password"
             :value (:password @fields)
             :on-change #(swap! fields assoc :password (-> % .-target .-value))}]]]
         [:div.field
          [:div.buttons.is-right
           [:button.button.is-link {:type "submit"} "Login"]]]]]])))
