(ns mordor-market-rest.handler
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:use [ring.util.response])
  (:use [cheshire.core])
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [mordor-market-rest.config :refer [db-config]]
            [ring.middleware.json :as middleware]
            [clojure.java.jdbc :as sql]
            [compojure.route :as route]))

(defn pool [config]
  (let [cdps (doto (ComboPooledDataSource.)
    (.setDriverClass (:className config))
    (.setJdbcUrl (str "jdbc:" (:subprotocol config) (:host config) (:port config) "/" (:subname config)))
    (.setUser (:user config))
    (.setPassword (:password config))
    (.setMaxPoolSize 2)
    (.setMinPoolSize 1)
    (.setInitialPoolSize 1))]
  {:datasource cdps}))

(def pooled-db(delay (pool db-config)))

(defn db-connection [] @pooled-db)

(defn root-function []
  (content-type (response "Testing root route.") "text/html"))

; GET handler functions

; (defn get-user [id] 
;   (content-type (response (str db-config)) "text/html")) ; TODO: Get users ID from request.

(defn get-user [id]
  (content-type 
    (sql/with-connection (db-connection)
    (response (sql/with-query-results results
       ["select * from users"])) "text/html"))) ; TODO: Get users ID from request.

; SQL-commands for user-handling. TODO: simplify to single command.
; cur.execute("SELECT * FROM users WHERE rfid = %s" % rfid)
; cur.execute("SELECT * FROM users WHERE rfid = %s" % rfid)
; cur.execute("SELECT balance FROM users WHERE rfid = %s" % rfid)

(defn get-product [id]
  (content-type (response (str "Product with id " id)) "text/html")) ; TODO: Get item id from request.

; TODO: This could likely be simplified into a single command, where null is returned were product not found.
; SQL-commands for first checking if product exists and then selecting the details of said product.
; cur.execute("SELECT * FROM products WHERE product_id = %s" % product_id)
; cur.execute("SELECT product_name, product_price FROM products WHERE product_id = %s" % product_id)

(defn get-empty [type]
  (content-type (response (str "No id of " type " specified")) "text/html")) ; TODO: String work here.

; POST handler functions

(defn make-purchase [id body]
  (content-type (response (str "Purchase made with " id " and body " body)) "text/html")) 

; SQL-command for updating users
; cur.execute("UPDATE users SET balance=%s WHERE rfid = %s" % (self.balance, self.rfid))

; SQL-command for adding purchase to history
; cur.execute("INSERT INTO history(participant_rfid, event, date, time) VALUES (%s, '%s', CURRENT_DATE, CURRENT_TIME)" % (self.rfid, w))

; RESPONSE-JSON

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
    (GET "/" [] (get-empty "USER"))
    (GET "/:id" [id] (get-user id))))
  (context "/product" [] (defroutes product-routes
    (GET "/" [] (get-empty "PRODUCT"))
    (GET "/:id" [id] (get-product id))))
  (context "/purchase/:id" [id] (defroutes purchase-routes
    (GET "/" [] (get-empty "PURCHASE"))
    (POST "/" {body :body} (make-purchase id body))))
  ;TODO: POST route for purchases with items in body.
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
