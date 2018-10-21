(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))

; Garden doesn't have grid-layout's fr unit by default,
; but it provides the defunit macro to create new units
; so let's define it
(defunit fr)

(defn strs
  "Returns a string representation of the given list of lists
  in a form suitable for grid-layout's grid-areas key, which needs
  quoted lists
  (I'm sure Garden's got a built-in way of doing this but I couldn't find it)"
  [lists]
  (apply str
   (map (fn [x] (str "\"" (string/join " " x) "\"")) lists)))


; --- rules ---


(defn add-units-rules [rules]
  (assoc rules :units
    {:.unit    {
                :justify-self    :start
                :display         :flex
                :justify-content :center
                :align-items     :center
                :min-width       (em 2)
                :border-radius   (px 4)
                :border          [[:solid (rgb 200 200 200) (px 1)]]
                :background      (rgb 150 150 150)
                :padding         (em 0.1)
                }
     :.em      {:background (hsl 150 50 50) :color (hsl 150 20 10)}
     :.px      {:background (hsl 10 70 80) :color (hsl 15 20 10)}
     :.percent {:background (hsl 50 70 80) :color (hsl 15 20 10)}
     :.fr      {:background (hsl 200 70 80) :color (hsl 15 20 10)}}))

(defn add-rules [state]
  (assoc state :css
    (->
      {
        :.main
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
                  :grid-template-areas   (strs '[[tl t tr]
                                                 [l content r]
                                                 [bl b br]])
                  }
        :.demo-grid
                 {
                  :padding               (em 1)
                  :border-radius         (px 8)
                  :display               :grid
                  :background            (hsl 197 31 49)
                  :grid-template-columns [[(percent 20) (fr 1)]]
                  :grid-auto-rows        (em 1.3)
                  :grid-row-gap          (em 1)
                  :grid-column-gap       (em 1)
                  }
        :body    {
                  :margin      0
                  :padding     0
                  :background  (rgb 50 50 50)
                  :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
                  :font-size   (em 1)
                  :font-weight :normal
                  :cursor      :default
                  }
        :.button {:cursor :pointer}
        }
        add-units-rules)))