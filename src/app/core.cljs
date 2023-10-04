(ns app.core
  (:require [react-native :as rn]
            [uix.core :refer [$ defui]]))

(defui root []
  ($ rn/View {:style {:flex 1
                      :align-items :center
                      :justify-content :center}} 
    ($ rn/Text {:style {:font-size 32
                        :font-weight "500"
                        :text-align :center}}
      "Hello! 👋")))

(defn start []
  (.registerComponent rn/AppRegistry "{{app-name}}" (constantly root)))