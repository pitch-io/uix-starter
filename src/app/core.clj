(ns app.core
  (:require [org.httpkit.server :as http]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [uix.dom.server :as dom.server]
            [uix.core :as uix :refer [defui $]])
  (:gen-class))

(defui page [{:keys [title children]}]
  ($ :html
     ($ :head
        ($ :title title)
        ($ :meta {:name :viewport :content "width=device-width, initial-scale=1"})
        ($ :link {:rel :preconnect :href "https://fonts.googleapis.com"})
        ($ :link {:rel :preconnect :href "https://fonts.gstatic.com" :crossorigin true})
        ($ :link {:rel :stylesheet :href "https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"})
        ($ :link {:rel :stylesheet :href "/main.css"}))
     ($ :body
        ($ :#root
           children)
        ($ :script {:src "/js/main.js"}))))

(defroutes routes
  ;; In a real system, you would serve static files from a CDN
  (route/files "/" {:root "public"})
  ;; Api routes
  (GET "/api/server-time" req
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (str (java.util.Date.))})
  ;; SPA mode: Renders HTML page for all routes
  (GET "*" req
    (dom.server/render-to-string ($ page {:title "Hello, UIx!"}))))

(defn run-server []
  (let [port (or (some-> (System/getenv "PORT") parse-long)
                 8080)]
    (println (str "Server is listening on: http://localhost:" port))
    (http/run-server #'routes {:port port})))

(defn -main [& args]
  (run-server))

(comment
  (def stop-server (-main)))