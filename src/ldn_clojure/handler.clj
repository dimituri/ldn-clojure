(ns ldn-clojure.handler
  (:use compojure.core)
  (:require [clojure.string :as s]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn handle-reverse [{words "words"}]
  (let [join #(s/join " " %)]
    (some-> words (s/split #"\s+") reverse join)))

(defmulti handle-temp (fn [from to _] [from to]))
(defmethod handle-temp ["c" "f"] [_ _ value]
  (some-> (-> value (* 9/5) (+ 32)) str))
(defmethod handle-temp ["f" "c"] [_ _ value]
  (some-> (-> value (* 5/9) (- 32)) str))
(defmethod handle-temp :default [_ _ _] nil)

(defroutes app-routes
  (GET "/" [] "CLOJURE WEB SERVICE 9000")
  (GET "/reverse" {params :query-params}
       (handle-reverse params))
  (GET ["/temp/:from/:to/:value" :value #"-?(?:\d+.)?\d+"] [from to value]
       (handle-temp from to (-> value read-string double)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
