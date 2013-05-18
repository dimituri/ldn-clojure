(ns ldn-clojure.handler
  (:use compojure.core
        overtone.at-at)
  (:require [clojure.string :as s]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(def counters (agent {}))

(defn assoc-inc [map key]
  (let [val (get map key)]
    (assoc map key (if (nil? val) 1 (inc val)))))

(defn register [token]
  (send counters #(assoc-inc % token)))


(def scheduler-pool (mk-pool))
(every 5000 #(println @counters) scheduler-pool)


(defn handle-add [a b]
  (str (+ a b)))

(defn handle-reverse [{words "words"}]
  (let [join #(s/join " " %)]
    (some-> words (s/split #"\s+") reverse join)))

(defmulti handle-temp (fn [from to _] [from to]))
(defmethod handle-temp ["c" "f"] [_ _ value]
  (some-> (-> value (* 9/5) (+ 32)) str))
(defmethod handle-temp ["f" "c"] [_ _ value]
  (some-> (-> value (* 5/9) (- 32)) str))
(defmethod handle-temp :default [_ _ _] nil)


(defmacro decorate [f g]
  `(def ~f (-> ~f ~g)))

(defn with-register [f token]
  (fn [& args]
    (register token)
    (apply f args)))

(decorate handle-add (with-register "add"))
(decorate handle-reverse (with-register "reverse"))
(decorate handle-temp (with-register "temp"))


(let [number #"-?(?:\d+.)?\d+"
      string->double #(-> % read-string double)]
  (defroutes app-routes
    (GET "/" [] "CLOJURE WEB SERVICE 9000")
    (GET ["/add/:a/:b" :a number :b number] [a b]
         (handle-add (string->double a) (string->double b)))
    (GET "/reverse" {params :query-params}
         (handle-reverse params))
    (GET ["/temp/:from/:to/:value" :value number] [from to value]
         (handle-temp from to (string->double value)))
    (route/resources "/")
    (route/not-found "Not Found")))

(def app
  (handler/site app-routes))
