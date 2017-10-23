(ns faceviz.routes
  (:import goog.History)
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(def app-routes
  ["/" {"" :home
        "about" :about}])

(defn set-page! [route-match])


(def history
  (pushy/pushy set-page! (partial bidi/match-route app-routes)))

(defn start-routing! []
  (pushy/start! history))

