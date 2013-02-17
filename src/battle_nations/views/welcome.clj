(ns battle-nations.views.welcome
  (:require [battle-nations.views.common :as common]
            [noir.content.getting-started]
            [noir.response]
            [battle-nations.db_bridge])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to battle-nations"]))

(defpage "/my-page1" []
  (common/site-layout
   [:h1 "this is my first page!"]
   [:p "Hope you like it"]))

(defpage [:get  "/get-game"] {:keys [player-id]}
  (if-let [response-data (battle-nations.db_bridge/get-player-games player-id)]
    (noir.response/json response-data)
    (noir.response/json {:error "No current games found."})))

(defpage [:post "/current_game"] {:keys [login]}
  (if-let [login_id login]
    (noir.response/json {:login_ login_id})
  	(noir.response/empty)))
   



