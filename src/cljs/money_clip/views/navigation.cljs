(ns money-clip.views.navigation)

(defn menu []
  [:div#menu.flex.items-center
   [:div.flex-shrink-0
    [:img.h-8.w-8 {:src "https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=500" :alt "money clip"}]]
   [:div.hidden.md:block
    [:div.ml-10.flex.items-baseline.space-x-4
     [:a.bg-gray-900.text-white.rounded-md.px-3.py-2.text-sm.font-medium {:href "#" :aria-current "page"} "Dashboard"]
     [:a.text-gray-300.hover:bg-gray-700.hover:text-white.rounded-md.px-3.py-2.text-sm.font-medium {:href "#"} "Team"]
     [:a.text-gray-300.hover:bg-gray-700.hover:text-white.rounded-md.px-3.py-2.text-sm.font-medium {:href "#"} "Projects"]
     [:a.text-gray-300.hover:bg-gray-700.hover:text-white.rounded-md.px-3.py-2.text-sm.font-medium {:href "#"} "Calendar"]
     [:a.text-gray-300.hover:bg-gray-700.hover:text-white.rounded-md.px-3.py-2.text-sm.font-medium {:href "#"} "Reports"]]]])

(defn mobile-menu-button []
  [:button#mobile-menu-button.inline-flex.items-center.justify-center.rounded-md.bg-gray-800.p-2.text-gray-400.hover:bg-gray-700.hover:text-white.focus:outline-none.focus:ring-2.focus:ring-white.focus:ring-offset-2.focus:ring-offset-gray-800 {:type "button" :aria-controls "mobile-menu" :aria-expanded "false"}
   [:span.sr-only "Open main menu"]
   [:svg.block.h-6.w-6 {:fill "none" :viewBox "0 0 24 24" :stroke-width "1.5" :stroke "currentColor" :aria-hidden "true"}
    [:path {:stroke-linecap "round" :stroke-linejoin "round" :d "M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"}]]
   [:svg.hidden.h-6.w-6 { :fill "none" :viewBox "0 0 24 24" :stroke-width "1.5" :stroke "currentColor" :aria-hidden "true"}
    [:path {:stroke-linecap "round" :stroke-linejoin "round" :d "M6 18L18 6M6 6l12 12"}]]])

(defn mobile-menu []
  [:div#mobile-menu.md:hidden
   [:div.space-y-1.px-2.pb-3.pt-2.sm:px-3
    [:a.bg-gray-900.text-white.block.rounded-md.px-3.py-2.text-base.font-medium {:href "#" :aria-current "page"} "Dashboard"]
    [:a.text-gray-300.hover:bg-gray-700.hover:text-white.block.rounded-md.px-3.py-2.text-base.font-medium {:href "#"} "Team"]
    [:a.text-gray-300.hover:bg-gray-700.hover:text-white.block.rounded-md.px-3.py-2.text-base.font-medium {:href "#"} "Projects"]
    [:a.text-gray-300.hover:bg-gray-700.hover:text-white.block.rounded-md.px-3.py-2.text-base.font-medium {:href "#"} "Calendar"]
    [:a.text-gray-300.hover:bg-gray-700.hover:text-white.block.rounded-md.px-3.py-2.text-base.font-medium {:href "#"} "Reports"]]
   [:div.border-t.border-gray-700.pb-3.pt-4
    [:div.flex.items-center.px-5
     [:div.flex-shrink-0
      [:img.h-10.w-10.rounded-full {:src "img/user-profile.png?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80" :alt ""}]]
     [:div.ml-3
      [:div.text-base.font-medium.leading-none.text-white "User"]
      [:div.text-sm.font-medium.leading-none.text-gray-400 "user@example.com"]]
     [:button.ml-auto.flex-shrink-0.rounded-full.bg-gray-800.p-1.text-gray-400.hover:text-white.focus:outline-none.focus:ring-2.focus:ring-white.focus:ring-offset-2.focus:ring-offset-gray-800 {:type "button"}
      [:span.sr-only "View notifications"]
      [:svg.h-6.w-6 {:fill "none" :viewBox "0 0 24 24" :stroke-width "1.5" :stroke "currentColor" :aria-hidden "true"}
       [:path {:stroke-linecap "round" :stroke-linejoin "round" :d "M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"}]]]]
    [:div.mt-3.space-y-1.px-2
     [:a.block.rounded-md.px-3.py-2.text-base.font-medium.text-gray-400.hover:bg-gray-700.hover:text-white {:href "#"} "Your Profile"]
     [:a.block.rounded-md.px-3.py-2.text-base.font-medium.text-gray-400.hover:bg-gray-700.hover:text-white {:href "#"} "Settings"]
     [:a.block.rounded-md.px-3.py-2.text-base.font-medium.text-gray-400.hover:bg-gray-700.hover:text-white {:href "#"} "Signout"]]]])

(defn user-area []
  [:div#notifications.ml-4.flex.items-center.md:ml-6
   [:button.rounded-full.bg-gray-800.p-1.text-gray-400.hover:text-white.focus:outline-none.focus:ring-2.focus:ring-white.focus:ring-offset-2.focus:ring-offset-gray-800 {:type "button"}
    [:span.sr-only "View notifications"]
    [:svg.h-6.w-6 {:fill "none" :viewBox "0 0 24 24" :stroke-width "1.5" :stroke "currentColor" :aria-hidden "true"}
     [:path {:stroke-linecap "round" :stroke-linejoin "round" :d "M14.857 17.082a23.848 23.848 0 005.454-1.31A8.967 8.967 0 0118 9.75v-.7V9A6 6 0 006 9v.75a8.967 8.967 0 01-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 01-5.714 0m5.714 0a3 3 0 11-5.714 0"}]]]
   [:div#user-profile.relative.ml-3
    [:div
     [:button#user-menu-button.flex.max-w-xs.items-center.rounded-full.bg-gray-800.text-sm.focus:outline-none.focus:ring-2.focus:ring-white.focus:ring-offset-2.focus:ring-offset-gray-800 {:type "button" :data-dropdown-toggle "user-profile-dropdown" :data-dropdown-hide "" :aria-expanded "false" :aria-haspopup "true"}
      [:span.sr-only "Open user menu"]
      [:img.h-8.w-8.rounded-full {:src "img/user-profile.png?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80" :alt ""}]]]
    [:div#user-profile-dropdown.hidden.absolute.right-0.z-10.mt-2.w-48.origin-top-right.rounded-md.bg-white.py-1.shadow-lg.ring-1.ring-black.ring-opacity-5.focus:outline-none {:role "menu" :aria-orientation "vertical" :aria-labelledby "user-menu-button" :tabIndex "-1"}
     [:a#user-menu-item-0.block.px-4.py-2.text-sm.text-gray-700 {:href "#" :role "menuitem" :tabIndex "-1"} "Your Profile"]
     [:a#user-menu-item-1.block.px-4.py-2.text-sm.text-gray-700 {:href "#" :role "menuitem" :tabIndex "-1"} "Settings"]
     [:a#user-menu-item-2.block.px-4.py-2.text-sm.text-gray-700 {:href "#" :role "menuitem" :tabIndex "-1"} "Sign out"]]]])

(defn navbar []
  [:nav.bg-gray-800
   [:div.mx-auto.max-w-7xl.px-4.sm:px-6.lg:px-8
    [:div.flex.h-16.items-center.justify-between
     [menu]
     [:div.hidden.md:block [user-area]]
     [:div.-mr-2.flex.md:hidden [mobile-menu-button]]]]
   [mobile-menu]])
