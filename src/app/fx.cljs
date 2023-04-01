(ns app.fx
  (:require
    [clojure.edn :as edn]
    [re-frame.core :as rf]))

(rf/reg-cofx :time/now
  (fn [cofx]
    (assoc cofx :time/now (js/Date.now))))

(rf/reg-cofx :store/todos
  (fn [cofx store-key]
    (let [todos (edn/read-string (js/localStorage.getItem store-key))]
      (assoc cofx :store/todos todos))))

(defn store-todos [store-key]
  (rf/->interceptor
    :id :store/set-todos
    :after (fn [context]
             (js/localStorage.setItem store-key (-> context :effects :db :todos str))
             context)))
