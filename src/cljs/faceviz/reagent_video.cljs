(ns faceviz.reagent-video
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as r]
            [cljs.core.async :as async]))

(def TIME-REFRESH-INTERVAL-MS
  34)

;; note: there is an event fired on the video element called
;; timeupdate, but it can be as slow as 4Hz, which is just
;; not good enough. so instead we periodically check the
;; currentTime of the element and update an atom.

;; TODO: use add-watch! on the atom to detect when it gets reset from outside and update element
;; TODO: work out when to use the term "dom node" and when to use "element" =P

(defn video-player-old
  [player-state*]
  (r/with-let [dom-node-chan        (async/chan)            ;; for sending the dom-node to the refresh go block
               stop-refreshing-chan (async/chan)            ;; see the "finally" form below for where this is used
               refresh-time-process (go-loop []
                                      (when-let [element (:video-element @player-state*)]
                                        (let [time         (.-currentTime element)
                                              time-diff-ms (* 1000 (Math/abs (- time (:current-time @player-state*))))]
                                          ;; this check is to deal with a race condition where someone reset
                                          ;; the current time outside the element, and then this process
                                          ;; swaps it right back
                                          ;(println "diff: " time-diff-ms)
                                          ;(println "time: " time)
                                          ;(println)
                                          (when (> time-diff-ms 0)
                                            (swap! player-state* assoc :current-time time))))

                                      (let [[stop-refreshing _] (async/alts!
                                                                  [stop-refreshing-chan
                                                                   (async/timeout TIME-REFRESH-INTERVAL-MS)])]
                                        (when-not stop-refreshing
                                          (recur))))

               outside-change-watch (add-watch player-state*
                                               :sync-video-element-with-atom
                                               (fn [_ _ old new]
                                                 (let [{old-time :current-time} old
                                                       {new-time :current-time} new
                                                       time-diff-ms                 (* 1000 (Math/abs (- old-time new-time)))
                                                       time-has-changed             (not= old-time new-time)
                                                       native-element-changing-time (or (:seeking @player-state*)
                                                                                        (:playing @player-state*))]
                                                   (when (> time-diff-ms 34)
                                                     ;(println "atom watcher fired!!!")
                                                     ;(not native-element-changing-time)))
                                                     ;  (println "i am resetting element")
                                                     (set! (.-currentTime (:video-element @player-state*))
                                                           new-time)))))]

    [:video
     {:muted          true                                  ;; trust me
      :controls       true
      :id             "main-video"
      ;; TODO: set w/h after on load
      :width          640
      :height         341
      :on-loaded-data (fn [event]
                        (swap! player-state*
                               assoc
                               :loaded-first-frame true
                               :video-element (.-target event)))
      ;; thought about a playback state enum here, but you can be playing and seeking at the same time
      :on-playing     (fn [event]
                        (println "playing")
                        (swap! player-state* assoc :playing true))
      :on-pause       (fn [event]
                        (println "pause")
                        (swap! player-state* assoc :playing false))
      :on-seeking     (fn [event]
                        (println "seekING")
                        (swap! player-state* assoc :seeking true))
      :on-seeked      (fn [event]
                        (println "seekED")
                        (swap! player-state* assoc :seeking false))
      :src            "https://ia800502.us.archive.org/10/items/WebmVp8Vorbis/webmvp8.webm"}]
    (finally
      (remove-watch player-state* :sync-video-element-with-atom)
      (async/put! stop-refreshing-chan true))))

(defprotocol IControlVideo
  (play [this])
  (pause [this])
  (current-time [this]))

(defrecord VideoPlayer [video-element react-class]
  IControlVideo
  (play [this]
    (.play video-element))
  (pause [this]
    (.pause video-element))
  (current-time [this]
    (.-currentTime video-element))

  ;; used by reagent to give it back a React Component to mount
  IFn
  (-invoke [this]
    react-class))


;; does reagent work on protocols? can I extend it to this one?



(defn video-player []
  (let [dom-node        (atom nil)
        react-component (r/create-class {:component-did-mount (fn [this] (reset! dom-node (r/dom-node this)))
                                         :reagent-render      (fn [state]
                                                                [:p "yo" state])})]
    (->VideoPlayer dom-node react-component)))


(defn video-player-container [state*]
  (let [vp (video-player)]
    [:div
     [vp 1]]))





;; video player component
;; returns