(ns faceviz.reagent-video-test
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [clojure.test :as test :refer [deftest is testing]]
            [clojure.core.async :as async]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [reagent.core :as r]
            [faceviz.reagent-video :as r-video]))


;; either poll the atom, or add an event handler to the dom
;; to check when the video is actually reading and exposing a
;; currentTime?

(defn poll-atom
  [atom-to-poll get-in-keys]
  (go-loop [attempt-num 3]
    (async/<! (async/timeout 200))
    (if-let [value (get-in @atom-to-poll get-in-keys)]
      value
      (when-not (zero? attempt-num)
        (recur (dec attempt-num))))))

(deftest mounted-player-sanity-check
  (let [root-node     (.createElement js/document "div")
        player-state* (r/atom {})]
    (test/async done
      (let [mounted-component (r/render [r-video/video-player player-state*]
                                        root-node)
            dom-node          (r/dom-node mounted-component)]
        (go
          (try
            (testing "loading the video updates the player-state atom"
              (let [loaded (async/<! (poll-atom player-state* [:loaded-first-frame]))]
                ;; note: can't do <! inside the `is` macro, otherwise I wouldn't use this let block
                (is loaded)))

            (testing "current-time is set correctly on load"
              (is (= (:current-time @player-state*)
                     (.-currentTime dom-node))))

            (testing "changing the current-time in the atom updates the video element state"

              (swap! player-state* assoc :current-time 10)

              ;; give the atom watcher a chance to execute before making assertions
              (async/<! (async/timeout 20))

              (is (= (:current-time @player-state*)
                     (.-currentTime (r/dom-node mounted-component)))))

            (testing "video element is set on the atom"
              (is (= dom-node (:video-element @player-state*))))

            (testing "when the video element starts playing, the current-time updates"
              (let [time-before-play (:current-time @player-state*)]
                (.play dom-node)
                (println "playing")
                (async/<! (async/timeout 100))
                (println "playing!")
                (is (> (:current-time @player-state*)
                       time-before-play))))





            (catch js/Error e
              (println (.-stack e))
              (is (not e)))
            (finally
              (r/unmount-component-at-node dom-node)
              (done))))))))
