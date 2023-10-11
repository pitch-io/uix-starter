(ns preload
    (:require [uix.dev]))

;; Initializes fast-refresh runtime.
(defonce ^:export __init-fast-refresh!
  (do (uix.dev/init-fast-refresh!)
      nil))

;; Called by shadow-cljs after every reload.
(defn ^:dev/after-load refresh []
  (uix.dev/refresh!))

;; Forwards cljs build errors to React Native's error view
(defn ^:export build-notify [{:keys [type report]}]
  (when (= :build-failure type)
    (js/console.error (js/Error. report))))
