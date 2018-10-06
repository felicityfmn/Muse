(ns rs.css
  "CSS things"
  (:require
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.stylesheet :refer [at-media]]
    [garden.types :as gtypes]
    [thi.ng.color.presets :as tcp]
    [thi.ng.color.presets.brewer :as tcb]
    #?(:cljs [reagent.core :as r])
    [clojure.string :as string]))

(defn strs [l]
  (apply str (map (fn [x] (str "\"" (apply str (interpose " " x)) "\"")) l)))

(defn classes-brewer-colours
  "Returns a list of CSS rules
  assigning fill styles from the given Brewer colour
  scheme for the given classes (keywords)"
  ([scheme-name classes]
    (let [n (count classes)]
      (map
        (fn [class {:keys [r g b]}]
          [(str "." (name class)) {:fill (apply rgb (map (comp int (partial * 255)) [r g b]))}])
        classes (cycle (tcb/brewer-scheme-rgb scheme-name n))))))