(ns app.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :app/todos
  (fn [db _]
    (:todos db)))
