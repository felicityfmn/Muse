(ns rs.actions
  "
    This namespace has functions
    that change the state of the application

    Each function that changes the app state
    takes the current state app-state and a message map,
    and returns a new state

    This namespace can be tested in a normal Clojure REPL too
  "
  (:require
    [garden.core :as gc]
    #?(:cljs [reagent.ratom :as ra])
    [rs.css :as css :refer [fr]]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [clojure.string :as string]))

; this is the entire state of the application
; note the use of a Reagent atom when this runs
; as Clojurescript or a normal Clojure atom if this
; is running in Clojure
(defonce app-state
  #?(:cljs (ra/atom nil) :clj (atom nil)))

(defn add-colours [state]
  (assoc state :colours ["red" "green" "black" "orange" "blue" "purple" "cyan" "yellow"]))

(defn make-state
  ([]
   (make-state
     {
      :text    "Hello world!"
      :numbers
               {
                :x            7
                :circles      3
                :colour-index 0
                }
      :ranges
               {
                :x {:min 3 :max 33 :step 3}
                }
      :css
               {
                :grid
                {
                 :display               :grid
                 :background            (hsl 197 31 49)
                 :grid-template-columns [[(percent 30) (fr 1)]]
                 :grid-auto-rows        (em 1.3)
                 :grid-row-gap          (em 1)
                 :grid-column-gap       (em 1)
                 }
                }
      }))
    ([state]
      (-> state
        add-colours)))

(defn update!
  "
   This updates the application state using
   the given function a-function and the given message

   See the following functions for examples of
   functions that can change the state

   They're called from event handlers in rs.views
  "
  [a-function message]
  (swap! app-state (fn [current-state] (a-function current-state message))))

(defn initialize-state
  ([state message]
    (make-state)))

(defn change-text
  ([state {text :text :as message}]
    (println "change text" message)
    (assoc state :text text)))

(defn change-thing
  ([state {value :value path :path :as message}]
    (println "change value at path" path value (type value))
    (assoc-in state path value)))