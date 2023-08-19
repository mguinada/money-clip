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
 ::session-loading?
 (fn [db _]
   (:session/loading? db)))

(re-frame/reg-sub
 ::session-state
 :<- [::user]
 :<- [::session-loading?]
 (fn [[user loading?]]
   (cond
     (true? loading?) :loading
     user :authenticated
     :else :anonymous)))

(re-frame/reg-sub
 ::user-authenticated?
 :<- [::session-state]
 (fn [session]
   (= :authenticated session)))

(re-frame/reg-sub
 ::server-errors
 (fn [db]
   (:errors/server db)))
