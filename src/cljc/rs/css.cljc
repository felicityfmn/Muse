(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  ;(:require-macros [garden.def :refer [defcssfn]])
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))

; Garden doesn't have grid-layout's fr unit by default,
; but it provides the defunit macro to create new units
; so let's define it
(defunit fr)

(defunit rad)

;(defcssfn rotate)
;(defcssfn translate)

(defn strs
  "Returns a string representation of the given list of lists
  in a form suitable for grid-layout's grid-areas key, which needs
  quoted lists
  (I'm sure Garden's got a built-in way of doing this but I couldn't find it)"
  [lists]
  (apply str
         (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))


; --- rules ---




(defn add-canvas-rules [state]
  (-> state
      (assoc :canvas-rules
             {
              "#canvas"
                                {
                                 :border        [[(px 0) :solid (rgb 86 86 86)]]
                                 :background    (hsla 210 70 85 1)
                                 :border-radius (em 0)
                                 :width         (percent 90)
                                 :height        (px 500)
                                 :display       "table"

                                 }
              "#button-wrapper" {
                                 :display        "table-cell"
                                 :vertical-align "middle"
                                 }
              "#demo-button"    {
                                 :background    (hsl 109 100 70)
                                 :max-width     (px 100)
                                 :height        (px 50)
                                 :overflow      "hidden"
                                 :padding       (px 10)
                                 :display       "flex"
                                 :margin        "auto"
                                 :border-radius (em 1)
                                 :font-size     (pt 16)
                                 :border-width  (px 1)
                                 :box-shadow    [[(px 0) (px 0) (px 10) (hsl 0 0 0)]]
                                 :text-align    "center"
                                 :text-shadow [[(px 1) (px 0.5) (px 0.5) (hsl 0 0 0)]]
                                 }

              })

      ))



(defn add-rules [state]
  (assoc state :css
               (->
                 {
                  :.main
                                          {
                                           :background  "linear-gradient( to bottom, rgba( 216, 215, 215, 0.3 ), white )"
                                           :text-shadow [[(px 0) (px 0) (px 3) (hsl 0 0 0)] [(px 1) (px 0) (px 1) (hsl 65 49 67)]]
                                           :color       (hsl 0 0 100)
                                           :width       (percent 100)
                                           :height      (percent 100)
                                           :display     :flex
                                           :flex-flow   "column nowrap"

                                           }
                  :.demo-grid
                                          {
                                           :padding       (em 2)
                                           :border-radius (px 8)
                                           :display       :grid
                                           :background    (hsl 137 96 80)

                                           }
                  :body                   {
                                           :background  "linear-gradient( to top, grey, white )"
                                           :margin      (percent 1)
                                           :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
                                           :font-size   (em 1)
                                           :font-weight :normal
                                           :cursor      :default
                                           :zoom        0.8
                                           }
                  :.button                {
                                           :cursor      :pointer
                                           :width       (px 10)
                                           :height      (px 28)
                                           :margin-left (percent 1)

                                           }
                  ".button-refresh:hover" {}
                  "#text-demo"            {
                                           :color (hsl 20 30 10)

                                           }
                  :div.canvas-parameters  {
                                           :max-height (px 200)
                                           :padding    (px 10)
                                           }
                  :div.button-parameters  {
                                           :height  (percent 50)
                                           :padding (px 10)
                                           }

                  }
                 )))