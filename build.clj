(ns build
  (:require [clojure.tools.build.api :as b]))

(def build-dir "target")
(def class-dir (str build-dir "/classes"))
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (str build-dir "/app.jar"))

(defn uber [_]
  (b/delete {:path build-dir})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir
                  :ns-compile '[app.core]})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis})
  (println "Uberjar written to" uber-file))