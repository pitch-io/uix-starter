(ns preload
    (:require [react-native :as rn]
              [uix.dev]))

;; Initializes fast-refresh runtime.
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defonce __init-fast-refresh!
  (do (uix.dev/init-fast-refresh!)
      nil))

;; Programatically disabled React Native's hot reloading
;; Only on iOS, because:
;; - On Android there's bug that causes an infinite reload loop when calling `setHotLoadingEnabled`
;; - On Web `setHotLoadingEnabled` is not available
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defonce __disable-react-native-hot-reloading!
  (do (when (-> rn/Platform .-OS (= "ios"))
        (-> rn/NativeModules .-DevSettings (.setHotLoadingEnabled false)))
      nil))

;; Called by shadow-cljs after every reload.
(defn ^:export ^:dev/after-load refresh []
  (uix.dev/refresh!))

;; Forwards cljs build errors to React Native's error view
(defn ^:export build-notify [{:keys [type report]}]
  (when (= :build-failure type)
    (js/console.error (js/Error. report))))
