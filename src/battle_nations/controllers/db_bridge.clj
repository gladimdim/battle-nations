(ns battle-nations.controllers.db_bridge
  (:use [monger.collection :only [insert insert-batch]]
        [monger.operators])
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

(mg/connect!)

(mg/set-db! (monger.core/get-db "test"))
(insert "document" {:a 10})

(defn put-player-in-queue [player-id army]
  "Puts player by his id into game's queue"
  (insert "players_queue" {:player_id player-id :army army}))

(defn get-player-from-queue []
  "Returns a player object which was matched from players_queue"
  (monger.collection/find-one-as-map "players_queue" {}))

(defn remove-player-from-queue [player-id]
  "Removes player by his id from players_queue table."
   (monger.collection/remove "players_queue" {:player_id player-id}))
     
(def armies {"ukraine" (battle-nations.data.ukraine/init-army) "poland" (battle-nations.data.poland/init-army)})

(defn create-new-game [player-id-left player-id-right army-left army-right]
  "Creates new game with two player ids"
  (let [game-id (clojure.string/join "-" [player-id-left player-id-right (gensym)])]
	(monger.collection/insert "current_games"
                            {:game_id game-id,
                             :player_left player-id-left,
                             :player_right player-id-right,
                             :left_army_nation army-left
                             :left_army {:bank (armies army-left),
                                         :field {}
                                         },
                             :right_army_nation army-right
                             :right_army {:bank (armies army-right)
                                         :field {}
                                         },
                             :left_army_turn true
                                          
                            })
        game-id))

(defn start-new-game [player-id army] 
  "Is called when user wants to start new game. Checks if another player is in queue
  and if not - puts player-id into queue."
  (if-let [waiting-player (get-player-from-queue)]
    (when-not (= player-id (waiting-player :player_id))
    (let [game-id (create-new-game player-id (waiting-player :player_id) army (waiting-player :army))]
     (remove-player-from-queue (waiting-player :player_id))
     game-id
      ))
    ((put-player-in-queue player-id army))))


(defn get-player-games [player-id]
  "Get all current games for player-id."
  (monger.collection/find-maps "current_games" {$or [{:player_left player-id}, {:player_right player-id}]} {:_id 0}))
    
