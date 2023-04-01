(ns app.core
  (:require
    [cljs.spec.alpha :as s]
    [clojure.edn :as edn]
    [uix.core :as uix :refer [defui $]]
    [uix.dom]))

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

(defui editable-text [{:keys [text text-style on-done-editing]}]
  (let [[editing? set-editing!] (uix/use-state false)
        [editing-value set-editing-value!] (uix/use-state "")]
    (if editing?
      ($ :input.todo-item-text-field
        {:value editing-value
         :auto-focus true
         :on-change (fn [^js e]
                      (set-editing-value! (.. e -target -value)))
         :on-key-down (fn [^js e]
                        (when (= "Enter" (.-key e))
                          (set-editing-value! "")
                          (set-editing! false)
                          (on-done-editing editing-value)))})
      ($ :span.todo-item-text
        {:style text-style
         :on-click (fn [_]
                     (set-editing! true)
                     (set-editing-value! text))}
        text))))

(s/def :todo/text string?)
(s/def :todo/status #{:unresolved :resolved})

(s/def :todo/item
  (s/keys :req-un [:todo/text :todo/status]))

(defui todo-item
  [{:keys [created-at text status on-update-todos] :as props}]
  {:pre [(s/valid? :todo/item props)]}
  ($ :.todo-item
    {:key created-at}
    ($ :input.todo-item-control
      {:type :checkbox
       :checked (= status :resolved)
       :on-change (fn [_]
                    (on-update-todos #(update-in % [created-at :status] {:unresolved :resolved
                                                                         :resolved :unresolved})))})
    ($ editable-text
      {:text text
       :text-style {:text-decoration (when (= :resolved status) :line-through)}
       :on-done-editing (fn [value]
                          (on-update-todos #(assoc-in % [created-at :text] value)))})
    ($ :button.todo-item-delete-button
      {:on-click (fn [_]
                   (on-update-todos #(dissoc % created-at)))}
      "×")))

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


(defui app []
  (let [[todos set-todos!] (use-persistent-state "uix-starter/todos" (sorted-map-by >))]
    ($ :.app
      ($ header)
      ($ text-field {:on-add-todo set-todos!})
      (for [[created-at todo] todos]
        ($ todo-item
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
