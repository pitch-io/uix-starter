(ns app.core
  (:require [react-native :as rn]
            [shadow.expo :as expo]
            [uix.core :refer [$ defui] :as uix]))

(defui counter []
  (let [[count set-count!] (uix/use-state 0)]
    ($ rn/View {:style {:padding-top 44}}
       ($ rn/Text {:style {:font-size 18
                           :font-weight "500"
                           :text-align :center}}
          "You've counted to: " count)
       ($ rn/Button {:on-press #(set-count! inc)
                     :title "Tap here to ++"})
       ($ rn/Button {:on-press #(set-count! dec)
                     :title "Tap here to --"}))))

(defui root []
  ($ rn/View {:style {:flex 1
                      :align-items :center
                      :justify-content :center}}
     ($ rn/Text {:style {:font-size 32
                         :font-weight "500"
                         :text-align :center}}
        "Hello! 👋 ")
     ($ counter)))

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
