(ns money-clip.views.navigation
  (:require [re-frame.core :as re-frame]
            [money-clip.subs :as subs]))

(defn- navbar []
  [:nav
   {:id "navbar-main", :class "navbar is-fixed-top"}
   [:div
    {:class "navbar-brand"}
    [:a
     {:class "navbar-item is-hidden-desktop jb-aside-mobile-toggle"}
     [:span
      {:class "icon"}
      [:i {:class "mdi mdi-forwardburger mdi-24px"}]]]
    [:div
     {:class "navbar-item has-control"}
     [:div
      {:class "control"}
      [:input {:placeholder "Search everywhere...", :class "input"}]]]]
   [:div
    {:class "navbar-brand is-right"}
    [:a
     {:class "navbar-item is-hidden-desktop jb-navbar-menu-toggle",
      :data-target "navbar-menu"}
     [:span {:class "icon"} [:i {:class "mdi mdi-dots-vertical"}]]]]
   [:div
    {:class "navbar-menu fadeIn animated faster", :id "navbar-menu"}
    [:div
     {:class "navbar-end"}
     [:div
      {:class
       "navbar-item has-dropdown has-dropdown-with-icons has-divider is-hoverable"}
      [:a
       {:class "navbar-link is-arrowless"}
       [:span {:class "icon"} [:i {:class "mdi mdi-menu"}]]
       [:span "Sample Menu"]
       [:span {:class "icon"} [:i {:class "mdi mdi-chevron-down"}]]]
      [:div
       {:class "navbar-dropdown"}
       [:a
        {:href "profile.html", :class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-account"}]]
        [:span "My Profile"]]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-settings"}]]
        [:span "Settings"]]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-email"}]]
        [:span "Messages"]]
       [:hr {:class "navbar-divider"}]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-logout"}]]
        [:span "Log Out"]]]]
     [:div
      {:class
       "navbar-item has-dropdown has-dropdown-with-icons has-divider has-user-avatar is-hoverable"}
      [:a
       {:class "navbar-link is-arrowless"}
       [:div
        {:class "is-user-avatar"}
        [:img
         {:src "https://avatars.dicebear.com/v2/initials/john-doe.svg",
          :alt "John Doe"}]]
       [:div {:class "is-user-name"} [:span "John Doe"]]
       [:span {:class "icon"} [:i {:class "mdi mdi-chevron-down"}]]]
      [:div
       {:class "navbar-dropdown"}
       [:a
        {:href "profile.html", :class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-account"}]]
        [:span "My Profile"]]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-settings"}]]
        [:span "Settings"]]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-email"}]]
        [:span "Messages"]]
       [:hr {:class "navbar-divider"}]
       [:a
        {:class "navbar-item"}
        [:span {:class "icon"} [:i {:class "mdi mdi-logout"}]]
        [:span "Log Out"]]]]
     [:a
      {:href
       "https://justboil.me/bulma-admin-template/free-html-dashboard/",
       :title "About",
       :class "navbar-item has-divider is-desktop-icon-only"}
      [:span {:class "icon"} [:i {:class "mdi mdi-help-circle-outline"}]]
      [:span "About"]]
     [:a
      {:title "Log out", :class "navbar-item is-desktop-icon-only"}
      [:span {:class "icon"} [:i {:class "mdi mdi-logout"}]]
      [:span "Log out"]]]]])

(defn- sidebar []
  [:aside
   {:class "aside is-placed-left is-expanded"}
   [:div
    {:class "aside-tools"}
    [:div {:class "aside-tools-label"} [:span [:b "Money"] "|clip"]]]
   [:div
    {:class "menu is-menu-main"}
    [:p {:class "menu-label"} "General"]
    [:ul
     {:class "menu-list"}
     [:li
      [:a
       {:href "index.html",
        :class "is-active router-link-active has-icon"}
       [:span {:class "icon"} [:i {:class "mdi mdi-desktop-mac"}]]
       [:span {:class "menu-item-label"} "Dashboard"]]]]
    [:p {:class "menu-label"} "Examples"]
    [:ul
     {:class "menu-list"}
     [:li
      [:a
       {:href "tables.html", :class "has-icon"}
       [:span
        {:class "icon has-update-mark"}
        [:i {:class "mdi mdi-table"}]]
       [:span {:class "menu-item-label"} "Tables"]]]
     [:li
      [:a
       {:href "forms.html", :class "has-icon"}
       [:span
        {:class "icon"}
        [:i {:class "mdi mdi-square-edit-outline"}]]
       [:span {:class "menu-item-label"} "Forms"]]]
     [:li
      [:a
       {:href "profile.html", :class "has-icon"}
       [:span {:class "icon"} [:i {:class "mdi mdi-account-circle"}]]
       [:span {:class "menu-item-label"} "Profile"]]]
     [:li
      [:a
       {:class "has-icon has-dropdown-icon"}
       [:span {:class "icon"} [:i {:class "mdi mdi-view-list"}]]
       [:span {:class "menu-item-label"} "Submenus"]
       [:div
        {:class "dropdown-icon"}
        [:span {:class "icon"} [:i {:class "mdi mdi-plus"}]]]]
      [:ul
       [:li [:a {:href "#void"} [:span "Sub-item One"]]]
       [:li [:a {:href "#void"} [:span "Sub-item Two"]]]]]]
    [:p {:class "menu-label"} "About"]
    [:ul
     {:class "menu-list"}
     [:li
      [:a
       {:href "https://github.com/vikdiesel/admin-one-bulma-dashboard",
        :target "_blank",
        :class "has-icon"}
       [:span {:class "icon"} [:i {:class "mdi mdi-github-circle"}]]
       [:span {:class "menu-item-label"} "GitHub"]]]
     [:li
      [:a
       {:href
        "https://justboil.me/bulma-admin-template/free-html-dashboard/",
        :class "has-icon"}
       [:span {:class "icon"} [:i {:class "mdi mdi-help-circle"}]]
       [:span {:class "menu-item-label"} "About"]]]]]])

(defn- titlebar []
  [:section
   {:class "section is-title-bar"}
   [:div
    {:class "level"}
    [:div
     {:class "level-left"}
     [:div {:class "level-item"} [:ul [:li "Admin"] [:li "Dashboard"]]]]
    [:div
     {:class "level-right"}
     [:div
      {:class "level-item"}
      [:div
       {:class "buttons is-right"}
       [:a
        {:href "https://github.com/vikdiesel/admin-one-bulma-dashboard",
         :target "_blank",
         :class "button is-primary"}
        [:span {:class "icon"} [:i {:class "mdi mdi-github-circle"}]]
        [:span "GitHub"]]]]]]])

(defn- herobar []
  [:section
   {:class "hero is-hero-bar"}
   [:div
    {:class "hero-body"}
    [:div
     {:class "level"}
     [:div
      {:class "level-left"}
      [:div {:class "level-item"} [:h1 {:class "title"} "Dashboard"]]]
     [:div
      {:class "level-right"}
      [:div {:class "level-item"}]]]]])

(defn navigation []
  (if @(re-frame/subscribe [::subs/user-authenticated?])
    [:div#navigation
     [navbar]
     [sidebar]
     [titlebar]
     [herobar]]))
