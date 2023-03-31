(ns app.hooks
  (:require
    [cljs.reader :as edn]
    [uix.core :as uix]))

(defn use-persistent-state
  "Loads initial state from local storage and persists every updated state value
  Returns a tuple of the current state value and an updater function"
  [store-key initial-value]
  (let [[value set-value!] (uix/use-state initial-value)]
    (uix/use-effect
      (fn []
        (let [value (edn/read-string (js/localStorage.getItem store-key))]
          (set-value! #(into % value))))
      [store-key])
    (uix/use-effect
      (fn []
        (js/localStorage.setItem store-key (str value)))
      [value store-key])
    [value set-value!]))
