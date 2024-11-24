(ns app.core
  (:require [uix.core :as uix :refer [defui $]]
            [uix.dom]))

(defn fetch-server-time []
  (-> (js/fetch "/api/server-time")
      (.then #(.text %))
      (.then #(js/alert %))))

(defui app []
  ($ :div {:style {:height "100vh"
                   :display :flex
                   :flex-direction :column
                   :gap 16
                   :align-items :center
                   :justify-content :center}}
    ($ :h1 "Hello, UIx!")
    ($ :button {:on-click fetch-server-time}
       "fetch server time")))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
