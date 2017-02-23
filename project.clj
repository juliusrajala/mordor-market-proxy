(defproject mordor-market-rest "0.1.0-SNAPSHOT"
  :description "This is a REST-api for a simple shop that stores data to a postgreSQL-db."
  :url "https://github.com/juliusrajala/mordor-market-proxy"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.1.2"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [cheshire "4.0.3"]]

  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler mordor-market-rest.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
