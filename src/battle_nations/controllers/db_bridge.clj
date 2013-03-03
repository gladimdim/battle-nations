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
                            {:game {:game_id game-id,
                             :player_left player-id-left,
                             :player_right player-id-right,

                             (keyword player-id-left) {:nation army-left
                                                       :bank (armies army-left),
                                                       :field {}
                                                       },
                             (keyword player-id-right) {:nation army-right
                                                        :bank (armies army-right)
                                                        :field {}
                                                        },
                             :left_army_turn true
                                          
                            }})
        game-id))

(defn start-new-game [player-id army] 
  "Is called when user wants to start new game. Checks if another player is in queue
  and if not - puts player-id into queue. If successfull - returns string game-id. If not - returns nil"
  (if-let [waiting-player (get-player-from-queue)]
    (if (= player-id (waiting-player :player_id))
      (hash-map :message "user already in queue")
    (let [game-id (create-new-game player-id (waiting-player :player_id) army (waiting-player :army))]
     (remove-player-from-queue (waiting-player :player_id))
     (hash-map :game_id game-id)
      ))
    (if (monger.result/ok? (put-player-in-queue player-id army))
      (hash-map :message "user put into queue")
      (hash-map :error "error during saving user to queue"))))


(defn get-player-games [player-id]
  "Get all current games for player-id."
  (monger.collection/find-maps "current_games" {$or [{:game.player_left player-id}, {:game.player_right player-id}]} {:_id 0}))

(defn get-game-by-id [game-id]
  "Get the whole game by its id"
  (first (monger.collection/find-maps "current_games" {:game.game_id game-id} {:_id 0})))

(defn get-army-for-user [player-id game]
  "Returns map of user's army in game (hash-map)"
  ((keyword player-id) (:game game)))

(defn get-bank-for-army [army]
  "Returns bank hash-map for army (hash-map)"
  (army :bank))

(defn enough-unit-qty? [bank unit]
  "Returns true if there is enough quantity of unit to put it on board. bank is a hash-map"
  (if (contains? bank (keyword unit))
    (> (bank (keyword unit)) 0)
    false))

(defn get-nation-by-player-id [game player-id]
  (((keyword player-id) (:game game)) :nation))

(defn reduce-unit-qty [bank unit]
  (assoc bank (keyword unit) (- (bank (keyword unit)) 1)))

(defn move-unit-to-position [bank army game-id unit position]
  "Moves unit in game to position. Game - hash-map, Game-id - string, position - hashmap, unit - string."
  (let [new-bank (reduce-unit-qty bank unit)]
    new-bank))
    
(update-game-with-new-data [new-user-data]
  

(defn place-new-unit [game-id player-id unit position]
  (let [game (get-game-by-id game-id)]
    (let [army (get-army-for-user player-id game)]
      (let [bank (get-bank-for-army army)]
         (if (enough-unit-qty? bank unit)
           (let [reduced-bank (reduce-unit-qty bank unit)]
            (let [new-field (merge (army :field) {(keyword unit) (merge (eval (symbol (str "battle-nations.data." (get-nation-by-player-id game player-id)) unit)) {:position [0, 0]})})]
              (monger.collection/update "current_games" {:game.game_id game-id} { $set {(keyword (symbol (str "game." player-id ".field"))) new-field}})
)))))))

    
