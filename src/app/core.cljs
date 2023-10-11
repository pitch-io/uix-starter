(ns app.core
  (:require [react-native :as rn]
            ["expo" :as expo]
            [uix.core :refer [$ defui] :as uix]))

(defui counter []
  (let [[count set-count!] (uix/use-state 0)]
    ($ rn/View {:style {:padding-top 44}}
       ($ rn/Text {:style {:font-size 18
                           :font-weight "500"
                           :text-align :center}}
          "You've counted to: " count)

       ($ rn/Pressable {:on-press #(set-count! inc)}
          ($ rn/Text {:style {:user-select "none"}}
             "Tap here to ++"))

       ($ rn/Pressable {:on-press #(set-count! dec)}
          ($ rn/Text {:style {:user-select "none"}}
             "Tap here to --")))))

(defui root []
  ($ rn/View {:style {:flex 1
                      :align-items :center
                      :justify-content :center}}
     ($ rn/Text {:style {:font-size 32
                         :font-weight "500"
                         :text-align :center}}
        "Hello! 👋 ")
     ($ counter)))

(defn ^:export init []
  (expo/registerRootComponent root))
