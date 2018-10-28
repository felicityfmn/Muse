(ns rs.server
  (:require
    [aleph.http :as http]
    [compojure.route :refer [files not-found]]
    [compojure.handler :refer [site]]
    [compojure.core :refer [routes GET POST DELETE ANY context]]
    [ring.middleware.defaults :as rmd]
    [hiccup.core :as hicp]))

(defonce state (atom {:hi :state-not-made}))

(defn make-state
  ([] {}))

(defn page [req]
  {:status  200
   :headers {"content-type" "text/html"}
   :body
            (hicp/html
              [:html [:head {} [:title "-"]
                      [:meta {:charset :UTF-8}]]
               [:body
                [:div#app
                 [:script {:src "js/main.js" :type "text/javascript"}]]]])})

(defn add-routes!
  [state]
  (assoc state :routes
               (->
                (routes
                  (GET "/" [] page)
                  (files "/" {:root "public"})
                  (not-found "404"))
                (rmd/wrap-defaults rmd/site-defaults))))

(defn add-server! [{config :config routes :routes :as state}]
  (assoc state :server
               (http/start-server routes config)))

(defn start!
  ([]
    (start! {:port 8080}))
  ([config]
   (when (:server @state)
    (.close (:server @state)))
   (reset! state
     (->
       (make-state)
       (assoc :config config)
       add-routes!
       add-server!))))