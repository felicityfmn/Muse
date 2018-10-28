(ns rs.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [oops.core :refer [oget]]
    [garden.core :as gc :refer [css]]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent defunit]]
    [garden.types :as gt]
    [garden.selectors :as gs]
    [rs.css :as rcss :refer [fr rad deg rotate3d perspective translate strs sassify-rule named?]]
    [rs.actions :as actions]
    [clojure.string :as string]
    [rs.css :as css]))

(defn css-view
  "
    Returns a CSS component
    from the given list of rules
    and optional flags
  "
  ([rules]
    (css-view nil {} rules))
  ([flags rules]
    (css-view nil flags rules))
  ([id flags rules]
    [:style (if id {:type "text/css" :id id} {:type "text/css"})
     (css flags
       (mapcat
         (fn [[f & _ :as l]]
           (if (or (string? f) (named? f))
             (map sassify-rule (partition 2 l)) l))
         (if (map? rules) rules (partition-by :identifier rules))))]))

(defn input-text-view
  "
    Returns a textarea component
    that changes the :text key of the state
  "
  [{v :value title :title path :path}]
  [:input.input.text-input
     {
       :type  :textarea
       :title title
       :value v
       :on-change
        (fn [e] (actions/handle-message! {:path path :value (oget e [:target :value])}))
      }])

(defn input-number-view
  "Returns an input view that converts to/from a number"
  [{v :value min :min max :max step :step title :title path :path}]
  [:input.input
   {
    :type  :range
    :title title
    :min   min
    :max   max
    :step  step
    :value v
    :on-change
           (fn [e]
             (actions/handle-message! {:path path :value (js/parseFloat (oget e [:target :value]))}))
    }])

(defn input-unit-view
  "Returns an input view that converts to/from em units"
  [{u :unit v :value min :min max :max step :step title :title path :path}]
  [:input.input
   {
    :type  :range
    :title (or title (str path))
    :min   min
    :max   max
    :step  step
    :value (get v :magnitude)
    :on-change
           (fn [e]
             (actions/handle-message! {:path path :value (u (js/parseFloat (oget e [:target :value])))}))
    }])

(defn unit-view [{:keys [unit magnitude]}]
  [:div {:title (name unit) :class (str "unit " (if (= "%" (name unit)) "percent" (name unit)))} (str magnitude)])

(defn listy-view
  "Returns a view to display a list of things"
  [a-list]
  (into [:div.list]
    (map
      (fn [v]
        [:div
         (cond
           (:magnitude v) [unit-view v]
           (:hue v)       (str "(hsl " (string/join " " [(:hue v) (:saturation v) (:lightness v)]) ")")
           (seqable? v)   [listy-view v]
           :otherwise     (str v))])
      a-list)))

(defn table-view
  "Returns a view to display a table
   of the given map's key-value pairs"
  [a-map]
  (into [:div.demo-grid]
    (mapcat
      (fn [[k v]]
       [[:div (str (name k))]
        (cond
          (:magnitude v) [unit-view v]
          (:hue v) [:div (str "(hsl " (string/join " " [(:hue v) (:saturation v) (:lightness v)]) ")")]
          (seqable? v) [:div [listy-view v]]
          :otherwise [:div (str v)])
        ])
      a-map)))

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs

   Each component has its own CSS where possible
  "
  ([] (root-view @actions/app-state))
  ([{{:keys [main demo units] :as css-rules} :css :as s}]
     [:div.root
       [css-view :main {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main]
       [:div.main
        [css-view :units {} units]
        [:div.button {:title "reinitialize everything!" :on-click (fn [e] (actions/handle-message! {:clicked :reinitialize}))} "🌅"]
        [:div.things
          [:div.input
            [input-unit-view {:unit px :min 0 :max 32 :step 1 :path [:css :demo :.demo-grid :border-radius] :value (get-in demo [:.demo-grid :border-radius])}]
            [input-unit-view {:unit percent :min 5 :max 50 :step 1 :path [:css :demo :.demo-grid :grid-template-columns 0 0] :value (get-in demo [:.demo-grid :grid-template-columns 0 0])}]
            [input-unit-view {:unit em :min 0.3 :max 4 :step 0.1 :path [:css :demo :.demo-grid :grid-row-gap] :value (get-in demo [:.demo-grid :grid-row-gap])}]
            [input-number-view {:min 0 :max 255 :step 1 :title "Hue" :path [:css :demo :.demo-grid :background :hue] :value (get-in demo [:.demo-grid :background :hue])}]
            ]
            [:div.thing
              [css-view :demo {} demo]
              [table-view (get demo :.demo-grid)]]]]]))