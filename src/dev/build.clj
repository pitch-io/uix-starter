(ns dev.build
  (:require [shadow.cljs.devtools.api :as shadow]))

(defn start-process [cmd]
  (let [;; Redirect output to stdout.
        ;; inheritIO also redirects stdin, some tools might not like that.
        builder (-> (ProcessBuilder. ^"[Ljava.lang.String;" (into-array String cmd))
        (.redirectError java.lang.ProcessBuilder$Redirect/INHERIT)
        (.redirectOutput java.lang.ProcessBuilder$Redirect/INHERIT))
        process (.start builder)]
    ;; Also stop processes when shadow-cljs is stopped.
    (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (.destroy process))))
    process))

;; New version - main function

(defn start
  {:shadow/requires-server true}
  [& _args]
  ;; TODO: Create libs.js file if doesn't exist here instead of package.json?
  (start-process ["yarn" "start:libs"])
  (shadow/watch :app)
  ::started)

(defn release
  []
  (shadow/release :app)
  (.waitFor (start-process ["yarn" "build:libs"])))

;; Old version - hooks

;; TODO: Hooks might run multiple times, for example if the shadow-cljs.edn is changed
;; -> multiple ESBuild processes
;; Consider "main ns" to run the process & Shadow-cljs, so no need for hooks?
(defn run-cmd-configure
  {:shadow.build/stage :configure}
  [build-state {:keys [cmd]}]
  (start-process cmd)
  build-state)

(defn run-cmd-flush
  {:shadow.build/stage :flush}
  [build-state {:keys [cmd]}]
  (let [process (start-process cmd)]
    (.waitFor process)
    build-state))
