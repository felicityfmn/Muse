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
    [garden.compression :refer [compress-stylesheet]]
    [rs.css :as rcss :refer [fr strs]]
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
     (css flags (map vec (partition 2 rules)))])
  ([flags css-rule-map keyz]
    [:style {:type "text/css" :scoped true}
     (css flags (map (juxt identity css-rule-map) keyz))]))

(defn css-things-view
  "Some static CSS rules for the table-of-things view"
  ([]
   [css-view
    [
     ".things"
       {
        :display               'grid
        :grid-area             :content
        :grid-template-columns [[(percent 20) (percent 80)]]
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
     :.a-circle
     {
      :fill   (rgb 20 255 100)
      :stroke :none
      }
     :.circles
     {
      :background :black
      }
     :.list {
               :display :flex
               :flex-flow [:row :wrap]
             }
     :.little-layouts
     {
      :padding (em 1)
      :display               :grid
      :grid-template-columns [(repeat 4 (fr 1))]
      :grid-template-rows    [(repeat 4 (fr 1))]
      :grid-column-gap       (em 2)
      :grid-row-gap          (em 2)
      :background            (rgb 50 50 50)
      }
     :.little-layout-content {:background (rgb 20 20 20) :justify-self :center :grid-area :content :font-size (em 1) :color (rgb 255 250 240)}
     :.l {:background (rgb 255 0 0)    }
     :.r {:background (rgb 255 250 0)  }
     :.t {:background (rgb 20 140 255) }
     :.b {:background (rgb 30 255 100) }
     :.tl {:background (rgb 250 30 200) :grid-area :tl}
     :.tr {:background (rgb 250 30 200) :grid-area :tr}
     :.bl {:background (rgb 250 30 200) :grid-area :bl}
     :.br {:background (rgb 250 30 200) :grid-area :br}
     ]]))


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

(defn circles-view
  "Returns a view of some circles"
  [circles]
  [:svg.thing.circles {:viewBox "-1 -1 2 2" :height 64 :width "100%"}
    (into [:g]
      (map
        (fn [i]
          [:circle.a-circle {:cx i :cy 0 :r (/ 0.9 circles)}])
        (range -1 1 (/ 2 circles))))])

(defn little-layouts-view
  "Returns a view of lots of variants of the given layout rule"
  ([little-layout-rule i n]
    (into
      [:div.thing.little-layouts
        [css-view (into [".little-layout" little-layout-rule]
          (mapcat
            (fn [x]
              [(str ".little-layout-" x)
               {:grid-template-columns
                (update-in (get little-layout-rule :grid-template-columns) [0 1]
                  (fn [{p :magnitude}] (percent (min 100 (+ p (* 0.3 (Math/pow x 2)))))))}])
            (range 0 n)))]]
     (map
       (fn [x]
         (into [:div {:class (str "little-layout little-layout-" x)}
           [:div.little-layout-content (str (+ x i))]
           [:div.tl] [:div.bl] [:div.tr] [:div.br]
           ] (map [[:div.l] [:div.r] [:div.t] [:div.b]] (map (fn [y] (mod (+ x y) 4)) (range 0 4)))))
       (range 0 n)))))

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs

   Each component has its own CSS where possible
  "
  ([] (root-view @actions/app-state))
  ([{{x :x :as numbers}                                      :numbers
     {grid-css :.demo-grid little-layout :little-layout units :units :as css-rules} :css}]
     [:div.root
       [css-view {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} css-rules [:body :.main :.button]]
       [:div.main
        [css-view {} units [:.unit :.em :.px :.percent :.fr]]
        [css-things-view]
        [:div.button {:title "reinitialize everything!" :on-click (fn [e] (actions/handle-message! {:clicked :reinitialize}))} "🌅"]
        [:div.things
          [:div.input
            [input-unit-view {:unit px :min 0 :max 32 :step 1 :path [:css :.demo-grid :border-radius] :value (get-in grid-css [:border-radius])}]
            [input-unit-view {:unit percent :min 5 :max 50 :step 1 :path [:css :.demo-grid :grid-template-columns 0 0] :value (get-in grid-css [:grid-template-columns 0 0])}]
            [input-unit-view {:unit em :min 0.3 :max 4 :step 0.1 :path [:css :.demo-grid :grid-row-gap] :value (:grid-row-gap grid-css)}]
            [input-number-view {:min 0 :max 255 :step 1 :title "Hue" :path [:css :.demo-grid :background :hue] :value (get-in grid-css [:background :hue])}]
            ]
            [:div.thing.grid-demo
              [css-view {} [:.demo-grid grid-css]]
              [table-view grid-css]]
          [input-unit-view {:unit percent :min 3 :max 20 :step 1 :path [:css :little-layout :grid-template-columns 0 1] :value (get-in css-rules [:little-layout :grid-template-columns 0 1])}]
          [little-layouts-view little-layout x 16]]
          ]]))