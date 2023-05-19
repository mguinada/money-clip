(ns money-clip.views.home
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [money-clip.events :as events]
            [money-clip.routes :as routes]
            [money-clip.subs :as subs]))

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [:div
      [:a {:on-click #(re-frame/dispatch [::events/navigate :about])} "go to About Page"]]]))

(defmethod routes/panels :home-panel [] [home-panel])

(defn about-panel []
  [:div
   [:h1 "This is the About Page."]
   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])} "go to Home Page"]]])

(defmethod routes/panels :about-panel [] [about-panel])

(defn sign-in-panel []
  (let [fields (r/atom {})]
    (fn []
      [:div#sign-in-form.flex.min-h-full.flex-col.justify-center.px-6.py-12.lg:px-8
       [:div.sm:mx-auto.sm:w-full.sm:max-w-sm
        [:img.mx-auto.h-10.w-auto {:src "https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=600", :alt "money clip"}]
        [:h2.mt-10.text-center.text-2xl.font-bold.leading-9.tracking-tight.text-gray-900 "Sign in to your account"]]
       [:div.mt-10.sm:mx-auto.sm:w-full.sm:max-w-sm
        [:form.space-y-6 {:on-submit (fn [e]
                                       (.preventDefault e)
                                       (re-frame/dispatch [::events/login (:email @fields) (:password @fields)]))}
         [:div
          [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "email"} "Email address"]
          [:div.mt-2
           [:input.block.w-full.rounded-md.border-0.py-1.5.text-gray-900.shadow-sm.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
            {:id "email", :name "email", :type "email", :autoComplete "email", :required true, :value (:email @fields), :on-change #(swap! fields assoc :email (-> % .-target .-value))}]]]
         [:div
          [:div.flex.items-center.justify-between
           [:label.block.text-sm.font-medium.leading-6.text-gray-900 {:for "password"} "Password"]]
          [:div.mt-2
           [:input.block.w-full.rounded-md.border-0.py-1.5.text-gray-900.shadow-sm.ring-1.ring-inset.ring-gray-300.placeholder:text-gray-400.focus:ring-2.focus:ring-inset.focus:ring-indigo-600.sm:text-sm.sm:leading-6
            {:id "password", :name "password", :type "password", :autoComplete "current-password", :required true, :value (:password @fields), :on-change #(swap! fields assoc :password (-> % .-target .-value))}]]]
         [:div
          [:button.flex.w-full.justify-center.rounded-md.bg-indigo-600.px-3.py-1.5.text-sm.font-semibold.leading-6.text-white.shadow-sm.hover:bg-indigo-500.focus-visible:outline.focus-visible:outline-2.focus-visible:outline-offset-2.focus-visible:outline-indigo-600
           {:type "submit"} "Sign in"]]]]])))

(defmethod routes/panels :sign-in-panel [] [sign-in-panel])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (routes/panels @active-panel)))
