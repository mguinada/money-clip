(ns money-clip.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::user
 (fn [db]
   (:user db)))

(re-frame/reg-sub
 ::session-state
 :<-[::user]
 (fn [user]
   (if (nil? user)
     :anonymous
     :authenticated)))

(re-frame/reg-sub
 ::user-authenticated?
 :<-[::session-state]
 (fn [session]
   (= :authenticated session)))
