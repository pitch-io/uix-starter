(ns app
  (:require [uix.core :refer [defui $]]
            [uix.dom]))

(defui app []
  (let [[state set-state!] (uix.core/use-state 0)]
    ($ :<>
       ($ :button {:on-click #(set-state! dec)} "-")
       ($ :span state)
       ($ :button {:on-click #(set-state! inc)} "+"))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render
  []
  (uix.dom/render-root ($ app) root))

(defn ^:export init
  []
  (render))
