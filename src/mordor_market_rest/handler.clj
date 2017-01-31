(ns mordor-market-rest.handler
  (:use [ring.util.response])
  (:require [compojure.core :refer :all]
            [mordor-market-rest.config :refer [db-config]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn root-function []
  (content-type (response "Testing root route.") "text/html"))

(def response-object 
  {:something "Test string thing yeah."
   :else "Not my string"})

(defn get-response []
  (content-type (response ( .toString response-object)) "text/html"))

(defroutes app-routes
  (GET "/" [] (root-function))
  (context "/test" [] (defroutes api-routes
    (GET "/" [] (get-response))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
