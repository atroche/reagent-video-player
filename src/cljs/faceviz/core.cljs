(ns faceviz.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [faceviz.events]
            [faceviz.subs]
            [faceviz.routes :as routes]
            [faceviz.views :as views]
            [faceviz.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/root-component]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/start-routing!)
  (dev-setup)
  (mount-root)
  (re-frame/dispatch-sync [:initialize-db]))

