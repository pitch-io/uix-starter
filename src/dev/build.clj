(ns dev.build)

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

(defn run-cmd-configure
  {:shadow.build/stage :configure}
  [build-state {:keys [cmd]}]
  (let [process (start-process cmd)]
    build-state))

(defn run-cmd-flush
  {:shadow.build/stage :flush}
  [build-state {:keys [cmd once]}]
  (let [process (start-process cmd)]
    (when once (.waitFor process))
    build-state))
