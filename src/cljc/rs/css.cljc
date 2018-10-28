(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :refer [css]]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]
    [clojure.string :as string]))

; define some CSS units missing from garden.units:

(defunit fr)
(defunit rad)
(defunit deg)

; and some CSS functions for animations

(defn rotate [& d]      (gt/->CSSFunction "rotate" d))
(defn translate [& d]   (gt/->CSSFunction "translate" d))
(defn scale [& d]       (gt/->CSSFunction "scale" d))
(defn translate3d [& d] (gt/->CSSFunction "translate3d" d))
(defn scale3d [& d]     (gt/->CSSFunction "scale3d" d))
(defn rotate3d [& d]    (gt/->CSSFunction "rotate3d" d))
(defn perspective [& d] (gt/->CSSFunction "perspective" d))

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


(defn add-units-rules [state]
  (assoc-in state [:css :units]
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


(defn animation-rules [duration-ms]
  (mapcat
    (fn [i v]
     [
      (gt/->CSSAtRule :keyframes
        {:identifier (str "rotating-thing-" i)
         :frames     [
                      [:0%   {:transform (apply rotate3d (conj v (rad 0))) :opacity 1}]
                      [:50%  {:transform (apply rotate3d (conj v (rad 3.1415926))) :opacity 0.3}]
                      [:100% {:transform (apply rotate3d (conj v (rad 6.266))) :opacity 1}]]})
      (str ".rotating-" i)
      {:animation-name            (str "rotating-thing-" i)
       :animation-duration        (ms duration-ms)
       :animation-iteration-count :infinite
       :animation-timing-function :linear}
      ])
      (range) (map (fn [x] [(Math/sin x) (Math/cos x) 0.3]) (range 0 6.2 (/ 1 16)))))


(defn add-grid-rules [state]
  (assoc-in state [:css :demo]
    {
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
        }}))


(defn add-main-rules [state]
  (assoc-in state [:css :main]
    {
     :body    {
               :margin      0
               :padding     0
               :background  (rgb 50 50 50)
               :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
               :font-size   (em 1)
               :font-weight :normal
               :cursor      :default
               }
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
     :.list {
               :display :flex
               :flex-flow [:row :wrap]
             }
     "input[type=range]"
     {
      :background :none
      :&
        {
         ::-moz-range-thumb
         {
           :background (rgb 100 100 100)
           :&
           {
            :hover
            {
              :background (rgb 150 150 150)
            }
           }
         }
         ::-webkit-slider-thumb
         {
          :background :yellow
         }
        }
      }
     :.button {:cursor :pointer}
     }))

