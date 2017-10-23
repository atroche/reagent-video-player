(ns faceviz.views
  (:require [goog.string :as gstring]
            [goog.string.format]
            [reagent.core :as r]
            [faceviz.reagent-video :as rv]))


(def sample-video-url
  "https://ia800502.us.archive.org/10/items/WebmVp8Vorbis/webmvp8.webm")

(defn info-panel [video-player]
  [:div
   [:button
    {:on-click (fn [] (rv/step-forward video-player 1))}
    "Forward one second"]

   [:button
    {:on-click (fn [] (rv/play video-player))}
    "Play"]

   [:button
    {:on-click (fn [] (rv/pause video-player))}
    "Pause"]

   [:span
    ;; note: calling rv/current-time derefs a ratom, so this will update
    ;; as the current-time does.
    (gstring/format "%.1f" (or (rv/current-time video-player) 0))]])

(defn root-component []
  (let [video-player (rv/video-player)]
    [:div
     [:canvas]
      
     [video-player {:src sample-video-url}]
     [info-panel  video-player]]))


