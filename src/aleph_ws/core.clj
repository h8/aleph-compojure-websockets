(ns aleph-ws.core
  (:use [lamina.core]
        [aleph.http]
        [compojure.core])
  (:require [compojure.route :as route]
            [ring.util.response :as response])
  (:gen-class))

(def broadcast-channel (channel))

(defn chat-handler [ch handshake]
  (receive ch
           (fn [name]
             (siphon (map* #(str name ": " %) ch) broadcast-channel)
             (siphon broadcast-channel ch))))

(defroutes my-routes
  (GET "/chat/" [] (wrap-aleph-handler chat-handler))
  (GET "/" [] (response/file-response "index.html" {:root "public"}))
  (route/not-found "Page not found"))

(defn -main
  [& args]
  (start-http-server (wrap-ring-handler my-routes)
                     {:port 8080 :websocket true}))
