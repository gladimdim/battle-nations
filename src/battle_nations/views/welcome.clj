(ns battle-nations.views.welcome
  (:require [battle-nations.views.common :as common]
            [noir.content.getting-started]
            [noir.response])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to battle-nations"]))

(defpage "/my-page1" []
  (common/site-layout
   [:h1 "this is my first page!"]
   [:p "Hope you like it"]))

(defpage [:get  "/get-game"] []
  (noir.response/json {"id" "gladimdim"
                       "game_id" "101010"
                       }))

(defpage [:post "/current_game"] {:keys [login]}
  (if-let [login_id login]
    (noir.response/json {:login_ login_id})
  	(noir.response/empty)))
   



