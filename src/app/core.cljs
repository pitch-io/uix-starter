(ns app.core
  (:require [react-native :as rn]
            [shadow.expo :as expo]
            [uix.core :refer [$ defui]]))

(defui root []
  ($ rn/View {:style {:flex 1
                      :align-items :center
                      :justify-content :center}}
     ($ rn/Text {:style {:font-size 32
                         :font-weight "500"
                         :text-align :center}}
        "Hello! 👋 ")))

(defn start
  {:dev/after-load true}
  []
  (expo/render-root ($ root)))

(defn ^:export init []
  ;; Disabling Hot Reloading so that Shadow CLJS can take over
  (-> rn/NativeModules
      .-DevSettings
      (.setHotLoadingEnabled false));

  (start))
