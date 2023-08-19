(ns money-clip.views.navigation
  (:require [re-frame.core :as re-frame]
            [money-clip.subs :as subs]))

(defn- navbar []
  [:nav#navbar-main.navbar.is-fixed-top
   [:div.navbar-brand
    [:a.navbar-item.is-hidden-desktop.jb-aside-mobile-toggle
     [:span.icon
      [:i.mdi.mdi-forwardburger.mdi-24px]]]
    [:div.navbar-item.has-control
     [:div.control
      [:input.input {:placeholder "Search everywhere..."}]]]]
   [:div.navbar-brand.is-right
    [:a.navbar-item.is-hidden-desktop.jb-navbar-menu-toggle {:data-target "navbar-menu"}
     [:span.icon [:i.mdi.mdi-dots-vertical]]]]
   [:div#navbar-menu.navbar-menu.fadeIn.animated.faster
    [:div.navbar-end
     [:div.navbar-item.has-dropdown.has-dropdown-with-icons.has-divider.has-user-avatar.is-hoverable
      [:a.navbar-link.is-arrowless
       [:div.is-user-avatar
        [:img {:src "https://avatars.dicebear.com/v2/initials/john-doe.svg" :alt "John Doe"}]]
       [:div.is-user-name [:span "John Doe"]]
       [:span.icon [:i.mdi.mdi-chevron-down]]]
      [:div.navbar-dropdown
       [:a
        {:href "profile.html" :class "navbar-item"}
        [:span.icon [:i.mdi.mdi-account]]
        [:span "My Profile"]]
       [:a.navbar-item
        [:span.icon [:i.mdi.mdi-settings]]
        [:span "Settings"]]
       [:a.navbar-item
        [:span.icon [:i.mdi.mdi-email]]
        [:span "Messages"]]
       [:hr.navbar-divider]
       [:a.navbar-item
        [:span.icon [:i.mdi.mdi-logout]]
        [:span "Log Out"]]]]
     [:a.navbar-item.has-divider.is-desktop-icon-only {:href "https://justboil.me/bulma-admin-template/free-html-dashboard/" :title "About"}
      [:span.icon [:i.mdi.mdi-help-circle-outline]]
      [:span "About"]]
     [:a.navbar-item.is-desktop-icon-only {:title "Log out"}
      [:span.icon [:i.mdi.mdi-logout]]
      [:span "Log out"]]]]])

(defn- sidebar []
  [:aside.aside.is-placed-left.is-expanded
   [:div.aside-tools
    [:div.aside-tools-label [:span [:b "Money"] "|clip"]]]
   [:div.menu.is-menu-main
    [:p.menu-label "General"]
    [:ul.menu-list
     [:li
      [:a.is-active.router-link-active.has-icon {:href "index.html"}
       [:span.icon [:i.md.mdi-desktop-mac]]
       [:span.menu-item-label "Dashboard"]]]]
    [:p.menu-label "Examples"]
    [:ul.menu-list
     [:li
      [:a.has-icon {:href "tables.html"}
       [:span.icon.has-update-mark.
        [:i.mdi.mdi-table]]
       [:span.menu-item-label "Tables"]]]
     [:li
      [:a.has-icon {:href "forms.html"}
       [:span.icon
        [:i.mdi.mdi-square-edit-outline]]
       [:span.menu-item-label "Forms"]]]
     [:li
      [:a.has-icon {:href "profile.html"}
       [:span.icon [:i.mdi.mdi-account-circle]]
       [:span.menu-item-label "Profile"]]]
     [:li
      [:a.has-icon.has-dropdown-icon
       [:span.icon [:i.mdi.mdi-view-list]]
       [:span.menu-item-label "Submenus"]
       [:div.dropdown-icon
        [:span.icon [:i.mdi.mdi-plus]]]]
      [:ul
       [:li [:a {:href "#void"} [:span "Sub-item One"]]]
       [:li [:a {:href "#void"} [:span "Sub-item Two"]]]]]]
    [:p.menu-label "About"]
    [:ul.menu-list
     [:li
      [:a.has-icon {:href "https://github.com/vikdiesel/admin-one-bulma-dashboard" :target "_blank"}
       [:span.icon [:i.mdi.mdi-github-circle]]
       [:span.menu-item-label "GitHub"]]]
     [:li
      [:a.has-icon {:href "https://justboil.me/bulma-admin-template/free-html-dashboard/"}
       [:span.icon [:i.mdi.mdi-help-circle]]
       [:span.menu-item-label "About"]]]]]])

(defn- titlebar []
  [:section.section.is-title-bar
   [:div.level
    [:div.level-left
     [:div.level-item [:ul [:li "Admin"] [:li "Dashboard"]]]]
    [:div.level-right
     [:div.level-item
      [:div.buttons.is-right
       [:a.button.is-primary {:href "https://github.com/vikdiesel/admin-one-bulma-dashboard" :target "_blank"}
        [:span.icon [:i.mdi.mdi-github-circle]]
        [:span "GitHub"]]]]]]])

(defn- herobar []
  [:section.hero.is-hero-bar
   [:div.hero-body
    [:div.level
     [:div.level-left
      [:div.level-item
       [:h1.title "Dashboard"]]]
     [:div.level-right
      [:div.level-item]]]]])

(defn navigation []
  (if @(re-frame/subscribe [::subs/user-authenticated?])
    [:div#navigation
     [navbar]
     [sidebar]
     [titlebar]
     [herobar]]))
