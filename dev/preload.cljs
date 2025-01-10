(ns preload
    (:require [react-native :as rn]))

;; Forwards cljs build errors to React Native's error view
(defn ^:export build-notify [{:keys [type report]}]
  (when (= :build-failure type)
    (js/console.error (js/Error. report))))

;; Disable fast refresh, since CLJS does this differently
(set! (.-onFastRefresh rn/DevSettings) (fn [& _args]))
