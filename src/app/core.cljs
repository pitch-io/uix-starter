(ns app.core
  (:require
    [uix.core :as uix :refer [defui $]]
    [uix.dom]
    [app.todo-item :as todo]
    [app.hooks :as hooks]))

(defui header []
  ($ :header.app-header
    ($ :img {:src "https://raw.githubusercontent.com/pitch-io/uix/master/logo.png"
             :width 32})))

(defui footer []
  ($ :footer.app-footer
    ($ :small "made with "
              ($ :a {:href "https://github.com/pitch-io/uix"}
                    "UIx"))))

(defui text-field [{:keys [on-add-todo]}]
  (let [[value set-value!] (uix/use-state "")]
    ($ :input.text-input
      {:value value
       :placeholder "Add a new todo and hit Enter to save"
       :on-change (fn [^js e]
                    (set-value! (.. e -target -value)))
       :on-key-down (fn [^js e]
                      (when (= "Enter" (.-key e))
                        (set-value! "")
                        (on-add-todo #(assoc % (js/Date.now) {:text value
                                                              :status :unresolved}))))})))

(defui app []
  (let [[todos set-todos!] (hooks/use-persistent-state "uix-starter/todos" (sorted-map-by >))]
    ($ :.app
      ($ header)
      ($ text-field {:on-add-todo set-todos!})
      (for [[created-at todo] todos]
        ($ todo/todo-item
          (assoc todo :created-at created-at
                      :key created-at
                      :on-update-todos set-todos!)))
      ($ footer))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
