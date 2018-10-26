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
    (css flags
         (mapcat
           (fn [[f & _ :as l]] (if (or (symbol? f) (string? f) (keyword? f)) (map vec (partition 2 l)) l))
           (partition-by :identifier rules)))])
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
      :display               :grid
      :grid-area             :content
      :grid-template-columns [[(percent 30) (fr 1)]]
      :grid-column-gap       (em 1)
      :grid-auto-rows        :auto
      :grid-row-gap          (em 2.5)
      :padding               (percent 4)

      }
     ".thing" {:align-self :start :justify-self :stretch}
     ".input" {:align-self :start}

     ]]))


(defn input-text-view
  "
    Returns a textarea component
    that changes the :text key of the state
  "
  [{v :value title :title path :path id :id}]
  [:input.input.text-input
   {
    :type  :textarea
    :id    id
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

(defn text-demo
  "takes the text from the text-input function and displays it in a component"
  [{path  :path
    id    :id
    value :value
    }]
  [:div {:id id} value]
  )


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
               (:hue v) (str "(hsl " (string/join " " [(:hue v) (:saturation v) (:lightness v)]) ")")
               (seqable? v) [listy-view v]
               :otherwise (str v))])
          a-list)))




(defn formatter [a-string]
  (string/capitalize (string/join " " (string/split a-string #"-"))))

(defn sliders-view [parameter-maps]
  (into [:div]
        (mapcat
          (fn [{unit :unit :as parameter-map}]
            [
             [:div.title (formatter (name (last (filter keyword? (:path parameter-map)))))]
             (if unit
               [input-unit-view parameter-map]
               [input-number-view parameter-map]
               )
             ])
          parameter-maps)))

;(defn colours [canvas-colours button-colours]
;  ([canvas-colours :hue :lightness]
;    [{
;      :hue       hue
;      :lightness lightness
;
;      }
;     (+ hue 180 )
;     (+ lightness 30)])
;
;  )

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs

   Each component has its own CSS where possible
  "
  ([] (root-view @actions/app-state))
  ([{{x :x :as numbers}                                :numbers
     params                                            :parameter-maps
     slider-parameters                                 :slider-parameters
     :slider-button-parameters                         :slider-button-parameters
     {grid-css :.demo-grid units :units :as css-rules} :css
     canvas-rules                                      :canvas-rules
     {t :text}                                         :input-text
     :as                                               state}]
   [:div.root
    [css-view
     {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} css-rules [:body :.main :.button]]
    [:div.main

     [css-things-view]
     [:div.button {:title "reinitialize everything!" :on-click (fn [e] (actions/handle-message! {:clicked :reinitialize}))} "🔄"]

     [:div.things
      [:div.canvas-parameters "Canvas settings"
       [css-view {:vendors ["webkit" "moz" "o" "ms"] :auto-prefix #{:appearance}} (map (fn [[k v]] [k v]) canvas-rules)]
       [sliders-view (map (fn [{path :path :as parameter}] (assoc parameter :value (get-in state path))) slider-parameters)]
       ]
      [:div {:id "canvas"}
       [:div {:id "button-wrapper"}
        [:div {:id "demo-button"} [text-demo {:id "text-demo" :path [:input-text :text] :value t}]]]]

      [:div.button-parameters "Button settings"
       [sliders-view (map (fn [{path :path :as parameter}] (assoc parameter :value (get-in state path))) slider-button-parameters)]
       ]


      [input-text-view {:id "input-text" :path [:input-text :text] :value t}]

      ]
     ]]))