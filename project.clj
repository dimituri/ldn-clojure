(defproject ldn-clojure "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [overtone/at-at "1.1.1"]
                 [compojure "1.1.5"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler ldn-clojure.handler/app})
