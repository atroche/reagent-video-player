(ns faceviz.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  :current-time
  (fn [db]
   (get-in db [:video :current-time])))

(re-frame/reg-sub
  :video-element
  (fn [db]
    (get-in db [:video :element])))


