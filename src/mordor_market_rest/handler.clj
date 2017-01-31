(ns mordor-market-rest.handler
  (:use [ring.util.response])
  (:require [compojure.core :refer :all]
            [mordor-market-rest.config :refer [db-config]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn root-function []
  (content-type (response "Testing root route.") "text/html"))

; GET handler functions

(defn get-user [id] 
  (content-type (response "Users data of id") "text/html")) ; TODO: Get users ID from request.

(defn get-product [id]
  (content-type (response "Product of id") "text/html")) ; TODO: Get item id from request.

(defn get-empty [type]
  (content-type (response "No id of TYPE specified"))) ; TODO: String work here.

; POST handler functions

(def response-object 
  {:something "Test string thing yeah."
   :else "Not my string"})

(defn get-response []
  (content-type (response ( .toString response-object)) "text/html"))

; Router function

(defroutes app-routes
  (GET "/" [] (root-function))
  (context "/test" [] (defroutes test-routes
    (GET "/" [] (get-response))))
  (context "/user" [] (defroutes user-routes
    (GET "/" [] (get-empty))))
  (context "/product" [] (defroutes product-routes
    (GET "/" [] (get-empty))))
  (context "/purchase" [] (defroutes purchase-routes
    (GET "/" [] (get-empty))))
  ;TODO: POST route for purchases with items in body.
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
