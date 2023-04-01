(ns app.core
  (:require
    [cljs.spec.alpha :as s]
    [uix.core :as uix :refer [defui $]]
    [uix.dom]
    [app.hooks :as hooks]
    [app.subs]
    [app.handlers]
    [app.fx]
    [app.db]
    [re-frame.core :as rf]))

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
                        (on-add-todo {:text value :status :unresolved})))})))

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
  [{:keys [created-at text status on-remove-todo on-set-todo-text] :as props}]
  {:pre [(s/valid? :todo/item props)]}
  ($ :.todo-item
    {:key created-at}
    ($ :input.todo-item-control
      {:type :checkbox
       :checked (= status :resolved)
       :on-change #(rf/dispatch [:todo/toggle-status created-at])})
    ($ editable-text
      {:text text
       :text-style {:text-decoration (when (= :resolved status) :line-through)}
       :on-done-editing #(on-set-todo-text created-at %)})
    ($ :button.todo-item-delete-button
      {:on-click #(on-remove-todo created-at)}
      "×")))

(defui app []
  (let [todos (hooks/use-subscribe [:app/todos])]
    ($ :.app
      ($ header)
      ($ text-field {:on-add-todo #(rf/dispatch [:todo/add %])})
      (for [[created-at todo] todos]
        ($ todo-item
          (assoc todo :created-at created-at
                      :key created-at
                      :on-remove-todo #(rf/dispatch [:todo/remove %])
                      :on-set-todo-text #(rf/dispatch [:todo/set-text %1 %2]))))
      ($ footer))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.db/default-db])
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
