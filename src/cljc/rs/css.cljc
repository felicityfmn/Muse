(ns rs.css
  "CSS things"
  (:require
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.stylesheet :refer [at-media]]
    [garden.types :as gtypes]
    [clojure.string :as string]))

(defunit fr)

(defn strs [lists]
  (apply str
   (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))