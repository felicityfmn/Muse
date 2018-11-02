(ns muse.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))


; Garden doesn't have grid-layout's fr unit by default,
; but it provides the defunit macro to create new units
; so let's define it

; define some CSS units missing from garden.units:

(defunit rad)
(defunit fr)
(defunit deg)

; and some CSS functions for animations

(defn rotate [& d] (gt/->CSSFunction "rotate" d))
(defn translate [& d] (gt/->CSSFunction "translate" d))
(defn scale [& d] (gt/->CSSFunction "scale" d))
(defn translate3d [& d] (gt/->CSSFunction "translate3d" d))
(defn scale3d [& d] (gt/->CSSFunction "scale3d" d))
(defn rotate3d [& d] (gt/->CSSFunction "rotate3d" d))
(defn perspective [& d] (gt/->CSSFunction "perspective" d))
(defn linear-gradient [& d] (gt/->CSSFunction "linear-gradient" d))


(defn strs
  "Returns a string representation of the given list of lists
  in a form suitable for grid-layout's grid-areas key, which needs
  quoted lists
  (I'm sure Garden's got a built-in way of doing this but I couldn't find it)"
  [lists]
  (apply str
         (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))

(defn named? [x]
  (or (keyword? x) (symbol? x)))

(defn as-str [x]
  (if (named? x)
    (name x)
    x))

(defn sassify-rule
  "Recurses through the given vector's second element
   which represents a CSS rule, transforming any child rules
   at the :& key into a form that Garden will compile like SASS"
  [[parent-selector {children :& :as rule}]]
  (if children
    [parent-selector (dissoc rule :&)
     (map (fn [[child-selector child-rule]]
            (sassify-rule [(str (if (keyword? child-selector)
                                  (if (namespace child-selector) "&::" "&:")
                                  "&") (as-str child-selector))
                           child-rule]))
          children)]
    [parent-selector rule]))



; --- rules ---




(defn add-canvas-rules [state]
  (-> state
      (assoc :canvas-rules
             {
              "#canvas"
                                {
                                 :border        [[(px 0) :solid (rgb 86 86 86)]]
                                 :background    (hsla 210 70 95 1)
                                 :border-radius (em 0)
                                 :width         (percent 90)
                                 :height        (px 500)
                                 :display       "table"}


              "#button-wrapper" {
                                 :display        "table-cell"
                                 :vertical-align "middle"}

              "#demo-button"    {
                                 :background    (hsl 30 90 55)
                                 :max-width     (px 100)
                                 :height        (px 50)
                                 :overflow      "hidden"
                                 :padding       (px 10)
                                 :display       "table"
                                 :margin        "auto"
                                 :border-radius (em 1)
                                 :font-size     (pt 16)
                                 :border-width  (px 1)
                                 :box-shadow    [[(px 0) (px 0) (px 10) (hsl 0 0 0)]]
                                 :text-align    "center"
                                 :text-shadow   [[(px 1) (px 0.5) (px 0.5) (hsl 0 0 0)]]}

              "#text-demo"      {
                                 :display        "table-cell"
                                 :vertical-align "middle"}})))






(defn add-rules [state]
  (assoc state :css-main-rules
               (->
                 {
                  :.main
                                          {
                                           ;:background  "linear-gradient( to bottom, rgba( 216, 215, 215, 0.3 ), white )"
                                           :text-shadow [[(px 0) (px 0) (px 3) (hsl 0 0 0)] [(px 1) (px 0) (px 1) (hsl 65 49 67)]]
                                           :color       (hsl 0 0 100)
                                           :width       (percent 100)
                                           :height      (percent 100)
                                           :display     :flex
                                           :flex-flow   "column nowrap"}



                  ".things"
                                          {
                                           :background            (linear-gradient "to bottom" (rgba 216 215 215 0.1) :white)
                                           :display               :grid
                                           :grid-area             :content
                                           :grid-template-columns [[(percent 30) (percent 70)]]
                                           :grid-column-gap       (em 1)
                                           :grid-auto-rows        :auto
                                           :grid-row-gap          (em 2.5)
                                           :padding               (percent 4)}


                  ".thing"                {:align-self :start :justify-self :stretch}
                  ".input"                {:align-self :start}
                  ".text-input"
                                          {
                                           :font-size  (em 1)
                                           :color      (rgb 255 252 250)
                                           :background (rgb 90 90 90)
                                           :padding    (em 1)
                                           :border     :none
                                           :width      (percent 80)}


                  :body                   {
                                           :animation-name            :gradient-flow
                                           :animation-duration        (ms 15000)
                                           :animation-iteration-count :infinite
                                           :animation-timing-function :linear
                                           ;:background                (linear-gradient "to top" :grey :white)
                                           :margin                    (percent 1)
                                           :font-family               ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
                                           :font-size                 (em 1)
                                           :font-weight               :normal
                                           :cursor                    :default
                                           :zoom                      0.8}

                  :.button                {
                                           :cursor      :pointer
                                           :width       (px 10)
                                           :height      (px 28)
                                           :margin-left (percent 1)}


                  ".button-refresh:hover" {}
                  "#text-demo"            {
                                           :color (hsl 0 0 100)}


                  :div.canvas-parameters  {
                                           :max-height (px 200)
                                           :padding    (px 10)}

                  :div.button-parameters  {
                                           :height  (percent 50)
                                           :padding (px 10)}})))






