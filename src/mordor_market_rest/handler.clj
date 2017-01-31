(ns mordor-market-rest.handler
  (:use [ring.util.response])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn root-function []
  (content-type (response "Testing root route.") "text/html"))

(defroutes app-routes
  (GET "/" [] (root-function))
  (context "/test" [] (defroutes api-routes
    (GET "/" [] "Test route")))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
