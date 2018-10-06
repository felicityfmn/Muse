(ns rs.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [oops.core :refer [oget]]
    [garden.core :as gc :refer [css]]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent]]
    [rs.css :as rcss :refer [strs]]
    [rs.actions :as actions]
    [clojure.string :as string]))

(defn css-view
  "
    Returns a CSS component
    from the given list of rules
    and optional flags
  "
  ([rules]
    (css-view {} rules))
  ([flags rules]
    [:style {:type "text/css" :scoped true}
      (css flags (map vec (partition 2 rules)))]))

(defn css-root-view
  "
    Static general CSS for the whole page
  "
  ([]
    [css-view {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select :appearance}}
    [
     "body" {
             :margin      0
             :padding     0
             :background  (rgb 50 50 50)
             :font-family "Gill Sans, Helvetica, Verdana, Sans Serif"
             :font-size   (em 1)
             :font-weight :normal
             :cursor      :default
             }
     ".main" {
              :background            (rgb 70 70 70)
              :color                 (rgb 255 250 210)
              :width                 "100%"
              :height                "100%"
              :display               :grid
              :grid-template-columns "0.1fr 1fr 0.1fr"
              :grid-template-rows    :auto
              :grid-column-gap       (em 1)
              :grid-row-gap          (em 2)
              :grid-template-areas   (strs '[[.    .    .]
                                             [. content .]
                                             [.    .    .]])
              }
     ]]))

(defn input-text-view [text]
  [:input.input.text-input
     {
      :type  :textarea
      :value text
      :on-change
       (fn [e]
         ; we have received an event object, e, which is a Javascript object
         ; we need to get its value and send it to the actions/update! function
         ; to change the app-state, using the function actions/change-text to do it
         (actions/update! actions/change-text {:text (oget e [:target :value])}))
      }])

(defn input-number-view
  "Returns an input view that converts to/from a number"
  [{v :value min :min max :max step :step path :path}]
  [:input.input
   {
    :type  :range
    :min   min
    :max   max
    :step  step
    :value v
    :on-change
           (fn [e]
             ; in this case we're going to send the given path to change too
             (actions/update! actions/change-thing
               {:path path :value (js/parseFloat (oget e [:target :value]))}))
    }])

(defn input-em-view
  "Returns an input view that converts to/from em units"
  [{v :value min :min max :max step :step path :path}]
  [:input.input
   {
    :type  :range
    :min   min
    :max   max
    :step  step
    :value (get v :magnitude)
    :on-change
           (fn [e]
             (actions/update! actions/change-thing
               {:path path :value (em (js/parseFloat (oget e [:target :value])))}))
    }])

(defn css-things-view
  "Some dynamic CSS rules for the table-of-things view"
  ([{colour-index :colour-index} colours]
   [css-view
    [
     ".things"
     {
      :display               :grid
      :grid-area             :content
      :grid-template-columns "40% 60%"
      :grid-column-gap       (em 1)
      :grid-auto-rows        :auto
      :grid-row-gap          (em 2.5)
      :background            (rgb 70 70 70)
      }
     ".thing" {:align-self :start :justify-self :stretch}
     ".input" {:align-self :start}
     ".text-input"
     {
      :font-size  (em 1)
      :color      (rgb 255 252 250)
      :background (rgb 90 90 90)
      :padding    (em 1)
      :border     :none
      }
     ".sample" {
                :background   (colours colour-index)
                :width        (px 64)
                :height       (px 64)
                :border-width (px 1)
                :border-color (rgb 200 200 200)
                :border-style :solid
                }
     ".number" {
                :font-size (em 4)
                }
     ".a-circle"
     {
      :fill   (rgb 20 255 100)
      :stroke :none
      }
     ".circles"
     {
      :background :black
      }
     ]]))

(defn css-grid-view
  "A tiny dynamic CSS rule just for the little table"
  [rule]
  [css-view
    [
      ".my-columns" rule
    ]])

(defn table-view
  "Returns a table to display some CSS rules"
  [a-map]
  (into [:div.my-columns]
    (mapcat
      (fn [[k v]]
       [[:div (str k)]
        [:div
          (cond
            (:magnitude v) (str (:magnitude v))
            :otherwise (str v))]
        ])
      a-map)))

(defn circles-view
  "Returns a view of some circles"
  [circles]
  [:svg.thing.circles {:viewBox "-1 -1 2 2" :height 64 :width "100%"}
    (into [:g]
      (map
        (fn [i]
          [:circle.a-circle {:cx i :cy 0 :r (/ 0.9 circles)}])
        (range -1 1 (/ 2 circles))))])

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs

   Each component has its own CSS
  "
  ([] (root-view @actions/app-state))
  ([{text :text {range-x :x} :ranges
       {x :x colour-index :colour-index circles :circles :as numbers} :numbers
        colours :colours
        {grid-css :grid} :css}]
     [:div.root
       [css-root-view]
       [:div.main
        [css-things-view numbers colours]
        [:div.things
         [input-text-view text]
           [:div.thing.message text]
         [input-number-view (merge range-x {:path [:numbers :x] :value x})]
           [:div.thing.number x]
         [input-number-view {:min 0 :max (dec (count colours)) :step 1 :path [:numbers :colour-index] :value colour-index}]
           [:div.thing.sample]
         [input-number-view {:min 3 :max 17 :step 1 :path [:numbers :circles] :value circles}]
           [circles-view circles]
         [:div.input
          [input-em-view {:min 0.3 :max 8 :step 0.3 :path [:css :grid :grid-column-gap] :value (:grid-column-gap grid-css)}]
          [input-em-view {:min 0.3 :max 4 :step 0.3 :path [:css :grid :grid-row-gap] :value (:grid-row-gap grid-css)}]]
           [:div.thing.grid-demo
            [css-grid-view grid-css]
            [table-view grid-css]]]
        ]]))