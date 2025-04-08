(ns app.core
  (:require [uix.core :as uix :refer [defui $]]
            [uix.dom]))

(defui app []
  ($ :h1 "Hello, UIx!"))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (uix.dom/render-root
    ($ uix/strict-mode
       ($ app))
    root))

(defn ^:export init []
  (render))
