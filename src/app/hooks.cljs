(ns app.hooks
  (:require ["use-sync-external-store/with-selector" :refer [useSyncExternalStoreWithSelector]]
            [scheduler]
            [re-frame.core :as rf]
            [reagent.ratom :as ratom]
            [reagent.impl.component :as impl.component]
            [uix.core :as uix]))

(defn- use-batched-subscribe
  "Takes an atom-like ref type and returns a function
  that adds change listeners to the ref"
  [^js ref]
  (uix/use-callback
    (fn [listener]
      ;; Adding an atom holding a set of listeners on a ref
      (let [listeners (or (.-react-listeners ref) (atom #{}))]
        (set! (.-react-listeners ref) listeners)
        (swap! listeners conj listener))
      (fn []
        (let [listeners (.-react-listeners ref)]
          (swap! listeners disj listener)
          ;; When the last listener was removed,
          ;; remove batched updates listener from the ref
          (when (empty? @listeners)
            (set! (.-react-listeners ref) nil)))))
    [ref]))

(defn- use-sync-external-store [subscribe get-snapshot]
  (useSyncExternalStoreWithSelector
    subscribe
    get-snapshot
    get-snapshot
    identity ;; selector, not using, just returning the value itself
    =)) ;; value equality check)

(defn- run-reaction [^js ref]
  (let [key "__rat"
        ^js rat (aget ref key)
        on-change (fn [_]
                    ;; When the ref is updated, execute all listeners in a batch
                    (when-let [listeners (.-react-listeners ref)]
                      (scheduler/unstable_scheduleCallback scheduler/unstable_ImmediatePriority
                                                           #(doseq [listener @listeners]
                                                              (listener)))))]
    (if (nil? rat)
      (ratom/run-in-reaction
        #(-deref ref) ref key on-change {:no-cache true})
      (._run rat false))))

(defn use-reaction
  "Takes Reagent's Reaction,
  subscribes UI component to changes in the reaction
  and returns current state value of the reaction"
  [reaction]
  (if impl.component/*current-component*
    ;; in case when the reaction runs in Reagent component
    ;; just deref it and let Reagent handle everything
    @reaction
    ;; otherwise manage subscription via hooks
    (let [subscribe (use-batched-subscribe reaction)
          get-snapshot (uix/use-callback #(run-reaction reaction) [reaction])]
      (use-sync-external-store subscribe get-snapshot))))

(defn use-subscribe
  "Takes re-frame subscription query e.g. [:current-document/title],
  creates an instance of the subscription,
  subscribes UI component to changes in the subscription
  and returns current state value of the subscription"
  [query]
  (let [sub (binding [ratom/*ratom-context* #js {}]
              (rf/subscribe query))
        ;; using an empty atom when re-frame subscription is not registered
        ;; re-frame will still print the error in console
        ref (or sub (atom nil))]
    (if ^boolean goog.DEBUG
      ;; deref re-frame subscription directly when the hook is evaluated in REPL
      ;; see `preload/init-repl!`
      (if (.-eval-in-repl? js/window)
        @sub
        (use-reaction ref))
      (use-reaction ref))))
