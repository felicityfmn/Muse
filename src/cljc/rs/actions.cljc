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


(defn make-state
  ([]
   (make-state
     {
      :parameters
      [
         {:unit px :min 0 :max 32 :step 1 :path [:css :demo :.demo-grid :border-radius]}
         {:unit percent :min 5 :max 50 :step 1 :path [:css :demo :.demo-grid :grid-template-columns 0 0]}
         {:unit em :min 0.3 :max 4 :step 0.1 :path [:css :demo :.demo-grid :grid-row-gap]}
         {:min 0 :max 255 :step 1 :title "Hue" :path [:css :demo :.demo-grid :background :hue]}
       ]
      }))
    ([state]
      (-> state
        css/add-main-rules
        css/add-units-rules
        css/add-grid-rules)))

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