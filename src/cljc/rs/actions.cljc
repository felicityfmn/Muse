(ns rs.actions
  "
    This namespace has functions
    that change the state of the application

    Each function that changes the app state
    takes the current state app-state and a message map,
    and returns a new state
  "
  (:require
    [garden.core :as gc]
    #?(:cljs [reagent.ratom :as ra])
    [rs.css :as css]
    [rs.colour :as colour]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [clojure.string :as string]))

; this is the entire state of the application
; note
(defonce app-state
  #?(:cljs (ra/atom {}) :clj (atom {})))

(defn add-colours [state]
  (assoc state :colours ["red" "green" "black" "orange" "blue" "purple" "cyan" "yellow"]))

(defn make-state
  ([]
   (make-state
    {
     :text "Hello world!"
     :numbers
           {
            :x            7
            :colour-index 0
            }
     :ranges
           {
            :x {:min 3 :max 31 :step 3}
            }
     }))
    ([state]
      (-> state
        add-colours)))

(defn update! [f message]
  (swap! app-state (fn [s] (f s message))))

(defn change-text
  ([state {text :text :as message}]
    (println "change text" message)
    (assoc state :text text)))

(defn change-number
  ([state {value :value path :path :as message}]
    (println "change number at path" path)
    (assoc-in state path value)))