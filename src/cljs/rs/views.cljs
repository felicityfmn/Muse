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
    [rs.colour :as colour]
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

(defn css-root-view []
  [css-view {:vendors ["webkit" "moz"]
                 :auto-prefix #{:column-width :user-select :appearance}}
         [
           "body" {
                    :margin  0
                    :padding 0
                    :background  (rgb 255 150 0)
                    :font-family "Gill Sans, Helvetica, Verdana, Sans Serif"
                    :font-size   (em 1)
                    :font-weight :normal
                    :cursor      :default
                    }
          ".main" {
                 :background            (rgb 250 250 10)
                 :color                 :red
                 :font-family           "monospace"
                 :width "100%"
                 :display               :grid
                 :grid-template-columns "32px auto auto 32px"
                 :grid-template-rows    :auto
                 :grid-column-gap       (em 1)
                 :grid-row-gap          (em 0.5)
                 :grid-template-areas   (strs '[[a b c] [d e f] [j content l] [m n o]])
                 }
          ".things"
            {
              :display :flex
              :grid-area :content
              :flex-flow "column wrap"
              :background :red
              :color :blue
            }
         ]])

(defn text-input-view [text]
  [:input
     {
      :type  :text
      :value text
      :size  32
      :on-change
       (fn [e]
         (actions/update! actions/change-text {:text (oget e [:target :value])}))
      }])

(defn range-input-view [{v :value min :min max :max step :step path :path}]
  [:input
     {
      :type :range
      :min min
      :max max
      :step step
      :value v
      :on-change
       (fn [e]
         (actions/update! actions/change-number
          {:path path :value (oget e [:target :value])}))
      }])

(defn inputs-css-view [{colour-index :colour-index} colours]
  [css-view
    [
      ".sample" {
                 :background (colours colour-index)
                 :width (px 64)
                 :height (px 64)
                 :border-width (px 1)
                 :border-color (rgb 200 200 200)
                 :border-style :solid
               }
     ".number" {
                 :font-size (em 4)
               }
    ]])

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs
  "
  ([] (root-view @actions/app-state))
  ([{text :text {range-x :x} :ranges
       {x :x colour-index :colour-index :as numbers} :numbers
        colours :colours}]
     [:div.root
       [css-root-view]
       [:div.main
        [:div.things
         [inputs-css-view numbers colours]
         [text-input-view text]
         [:div.message text]
         [range-input-view (merge range-x {:path [:numbers :x] :value x})]
         [range-input-view {:min 0 :max (dec (count colours)) :step 1 :path [:numbers :colour-index] :value colour-index}]
         [:div.number x]
         [:div.sample]]
        ]]))