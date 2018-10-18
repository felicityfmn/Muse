(ns rs.css
  "CSS things"
  (:refer-clojure :exclude [repeat])
  (:require
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))

(defunit fr)

(defn strs [lists]
  (apply str
   (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))