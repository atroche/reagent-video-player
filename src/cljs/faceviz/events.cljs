(ns faceviz.events
  (:require [re-frame.core :as re-frame]
            [faceviz.db :as db]))

(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  :video-element-loaded
    (fn [db event]
      (let [video-element (second event)]
        (assoc db :video {:element      video-element
                          :current-time 0}))))

(re-frame/reg-fx
  :seek-video
  (fn [{:keys [element current-time]}]
    (set! (.-currentTime element) current-time)))

(re-frame/reg-event-fx
  :video-time-changed
  (fn [{:keys [db]} [_ new-time]]
    (let [new-video (assoc (:video db) :current-time new-time)]
      {:db (assoc db :video new-video)})))

;(def STEP-SIZE-MS 16)
;
;(re-frame/reg-event-fx
;  :step-forward
;  (fn [world event]
;    (let [video         (get-in world [:db :video])
;          stepped-video (update video :current-time (partial + STEP-SIZE-MS))]
;      {:db         (assoc (:db world) :video stepped-video)
;       :seek-video stepped-video})))
;

