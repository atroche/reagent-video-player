(ns faceviz.views
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [cljs.core.async :as async]
            [goog.functions :refer [debounce]]
            [faceviz.reagent-video :as reagent-video]
            [goog.string :as gstring]
            [goog.string.format]
            [reagent.core :as r]))


(defn video-display [])

(defn video-with-boxes []
  [re-com/v-box
   :children [[video-display]
              #_[zorro/masker {:base-component video-display}
                 :size {:width  800
                        :height 640}
                 :tool :element
                 :elements [{:type   :rectangle,
                             :id     #uuid "417e5ec4-8a30-4ed7-ad77-f2e9121a2764",
                             :styles nil,
                             :path
                                     [{:point [0.8125 0.01607717041800643]}
                                      {:point [0.9875 0.01607717041800643]}
                                      {:point [0.9875 0.4565916398713826]}
                                      {:point [0.8125 0.4565916398713826]}]}]
                 :on-create (fn [element] (cljs.pprint/pprint element) element)]]])


(defn info-panel [player-state*]
  [:div
   [:button {:on-click (fn [] (swap! player-state* update :current-time inc))}
    "Forward one second"]
   (gstring/format "%.1f" (get @player-state* :current-time 0))])

(defn root-component []
  (println "root re-rendered or loaded")
  (let [video-player-state (r/atom {:playing            false
                                    :seeking            false
                                    :loaded-first-frame false
                                    :current-time       0})]

    [:div
     [reagent-video/video-player-container video-player-state]
     [info-panel video-player-state]]))

;; video player, pass with various options to constructor
;; get back:
;; the component to give to reagent
;; an interface on which to call the various methods we want


