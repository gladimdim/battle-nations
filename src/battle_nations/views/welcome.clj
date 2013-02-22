(ns battle-nations.views.welcome
  (:require [battle-nations.views.common :as common]
            [noir.content.getting-started]
            [noir.response]
            [battle-nations.controllers.db_bridge])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to battle-nations"]))

(defpage [:get  "/get-game"] {:keys [player-id]}
  (if-let [response-data (battle-nations.db_bridge/get-player-games player-id)]
    (noir.response/json response-data)
    (noir.response/json {:error "No current games found."})))

(defpage [:post "/current_game"] {:keys [login]}
  (if-let [login_id login]
    (noir.response/json {:login_ login_id})
  	(noir.response/empty)))

(defpage [:post "/want-to-play"] {:keys [player-id]} 
  "Get player-id and check if there is another player in queue. If not - put player-id into it. If yes - 
start new game with this player"
  (battle-nations.controllers.db_bridge/start-new-game player-id))
   



