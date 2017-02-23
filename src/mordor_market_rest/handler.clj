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
    ; (.setPassword (:password config))
    (.setMaxPoolSize 2)
    (.setMinPoolSize 1)
    (.setInitialPoolSize 1))]
  {:datasource cdps}))

(def pooled-db (delay (pool db-config)))

(defn db-connection [] @pooled-db)

(defn root-function []
  (content-type (response "Testing root route.") "text/html"))

; GET handler functions

(defn get-user [id]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["SELECT * FROM users where rfid = ?" id]
        (cond
          (empty? results) {:status 404}
          :else (response (first results)))))))

(defn get-product [id]
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["SELECT * FROM products where product_id = ?" id]
        (cond
          (empty? results) {:status 404, :title "Not found", :description (str "Product with id " id " was not found")}
          :else (response (first results)))))))

(defn get-all-products []
  (response
    (sql/with-connection (db-connection)
      (sql/with-query-results results
        ["select * from products"]
        (into [] results)))))

; POST handler functions

(defn update-history [event])

(defn update-user [user-id price])

(defn post-purchase [body]
  (response
    (cond (do (update-history) (update-user))
    (empty? results){:status 404, :title "Post failed", :description "Something went wrong with your purchase"}
    :else (response (first results)))))

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
    (GET "/:id" [id] (get-user id))))
  (context "/product" [] (defroutes product-routes
    (GET "/" [] (get-all-products))
    (GET "/:id" [id] (get-product id))))
  (context "/purchase/:id" [id] (defroutes purchase-routes
    (GET "/" [] (get-empty "PURCHASE"))
    (POST "/" {body :body} (post-purchase id body))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
