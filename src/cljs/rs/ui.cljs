(ns ^:figwheel-hooks rs.ui
  "
    This namespace starts the UI
  "
  (:require
    [reagent.core :as r]
    [rs.actions :as actions]
    [rs.views :as views]))

(enable-console-print!)

(defn ^:before-load before-start!
  [& q]
  (println "---before restart---"))

(defn ^:after-load start! []
  (reset! actions/app-state (actions/make-state))
  (r/render [views/root-view]
     (.getElementById js/document "app")))