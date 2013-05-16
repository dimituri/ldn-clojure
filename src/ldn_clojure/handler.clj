(ns ldn-clojure.handler
  (:use compojure.core)
  (:require [clojure.string :as s]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn handle-reverse [{words "words"}]
  (let [join #(s/join " " %)]
    (some-> words (s/split #"\s+") reverse join)))

(defroutes app-routes
  (GET "/" [] "CLOJURE WEB SERVICE 9000")
  (GET "/reverse" {params :query-params} (handle-reverse params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
