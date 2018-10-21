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
      }))
    ([state]
      (-> state
        add-colours
        css/add-rules)))

(defn initialize-state
  ([state message]
    (make-state)))

(defn change-thing
  ([state {value :value path :path :as message}]
    (assoc-in state path value)))

(defn choose-function
  "Work out what function to use for updating
  state based on the given message"
  ([{of-what-was-clicked-on :clicked :as msg}]
   (case of-what-was-clicked-on
     :reinitialize initialize-state
     change-thing)))

(defn handle-message
  "Returns a new state from the given state and message"
  [state message]
  ((choose-function message) state message))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
   (handle-message! message (choose-function message)))
  ([message a-function]
    (swap! app-state
      (fn [current-state] (a-function current-state message)))))

(defn animation! []
  #?(:cljs
     (.requestAnimationFrame js/window
      (fn []
        (swap! app-state (fn [s] (update-in s [:numbers :x] (fn [y] (mod (inc y) 255)))))
        (animation!)))
      :clj :no-animation-available-in-normal-Clojure))