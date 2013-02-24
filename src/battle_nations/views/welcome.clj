(ns battle-nations.views.welcome
  (:require [clj-json.core :as json]
            [battle-nations.controllers.db_bridge])
  (:use [noir.core :only [defpage]])
  (:use compojure.core)
  (:use ring.middleware.json-params)
  (:use ring.adapter.jetty))


(defn json-response [data & [status]]
  {:status (or status 200)
   :heaeers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes handler 
  (GET "/" []
       (json-response {"hello" "world"}))
  (GET "/get-game" [player-id]
  (if-let [response-data (battle-nations.controllers.db_bridge/get-player-games player-id)]
    (json-response response-data)
    (json-response {:error "No current games found."})))
  (PUT "/test" [name]
       (json-response {"hello" name}))
  (POST "/want-to-play" [player-id army]
        (if (and player-id army)
          (json-response {:game_id (battle-nations.controllers.db_bridge/start-new-game player-id army)} 200)
          (json-response {:error "Missing parameters in request"} 500))))

(def app 
  (-> handler 
      wrap-json-params))
