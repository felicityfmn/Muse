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
    #?(:cljs [oops.core :refer [oget ocall]])
    [rs.css :as css :refer [fr strs]]
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
      :text "Hello world!"
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
             :main
             {
              :background            (rgb 70 70 70)
              :color                 (rgb 255 250 210)
              :width                 (percent 100)
              :height                (percent 100)
              :display               :grid
              :grid-template-columns [[(em 2) (fr 1) (em 2)]]
              :grid-template-rows    :auto
              :grid-column-gap       (em 1)
              :grid-row-gap          (em 2)
              :grid-template-areas   (strs '[[tl t tr]
                                             [l content r]
                                             [bl b br]])
              }
             :little-layout
             {
              :background            (rgb 40 40 40)
              :color                 (rgb 255 250 210)
              :width                 (percent 100)
              :height                (percent 100)
              :display               :grid
              :grid-template-columns [[(fr 1) (percent 10) (fr 2)]]
              :grid-template-rows    [[(em 1) (fr 1) (em 1)]]
              :grid-column-gap       (em 0.3)
              :grid-row-gap          (em 0.3)
              :grid-template-areas   (strs '[[tl   t   tr]
                                             [l content r]
                                             [bl   b   br]])
              }
             :grid
             {
              :padding (em 1)
              :border-radius (px 8)
              :display               :grid
              :background            (hsl 197 31 49)
              :grid-template-columns [[(percent 20) (fr 1)]]
              :grid-auto-rows        (em 1.3)
              :grid-row-gap          (em 1)
              :grid-column-gap       (em 1)
              }
             }
      }))
    ([state]
      (-> state
        add-colours)))

(defn initialize-state
  ([state message]
    (make-state)))

(defn change-thing
  ([state {value :value path :path :as message}]
    (assoc-in state path value)))

(defn update!
  "
   This updates the application state using
   the given function a-function and the given message
  "
  [a-function message]
  (swap! app-state
    (fn [current-state] (a-function current-state message))))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([{of-what-was-clicked-on :clicked :as msg}]
   (handle-message! msg
    (case of-what-was-clicked-on
      :reinitialize initialize-state
      change-thing)))
  ([msg a-function]
    (update! a-function msg)))

(defn animation! []
  #?(:cljs
     (.requestAnimationFrame js/window
      (fn []
        (swap! app-state (fn [s] (update-in s [:numbers :x] (fn [y] (mod (inc y) 255)))))
        (animation!)))
      :clj :no-animation-available-in-normal-Clojure))