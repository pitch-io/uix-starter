(ns app.todo-item
  (:require [uix.core :as uix :refer [defui $]]
            [cljs.spec.alpha :as s]))

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
