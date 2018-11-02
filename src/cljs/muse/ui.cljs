(ns ^:figwheel-hooks muse.ui
  "
    This namespace starts the UI
  "
  (:require
    [reagent.core :as r]
    [muse.actions :as actions]
    [muse.views :as views]))

(defn ^:before-load before-start!
  [& q]
  (println "---before restart---"))

(defn ^:after-load start! []
  (when (nil? @actions/app-state)
    (do
      (reset! actions/app-state (actions/make-state))))




      ; comment this line to stop animation
      ;(actions/animation!)


  (r/render [views/root-view]
     (.getElementById js/document "app")))