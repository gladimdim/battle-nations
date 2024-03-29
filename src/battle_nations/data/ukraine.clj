(ns battle-nations.data.ukraine)

(defn init-army []
  (hash-map :infantry 3,
  :light_cavalry 3,
  :veteran 2,
  :healer 2,
  :super_unit 1)
)

(def healer 
{
 :level_life 100
 :range_attack_length 3
 :range_move 2
 :heal 35
 })

(def infantry 
  {
   :level_life 100
   :range_attack_length 3
   :range_attack_strength 30
   :range_move 1
   :melee_attack_strength 20
   })

(def light_cavalry
  {
   :level_life 100
   :range_attack_length 0
   :range_attack_strength 0
   :range_move 3
   :melee_attack_strength 30
   })

(def veteran 
  {
   :level_life 100
   :range_attack_length 3
   :range_attack_strength 40
   :range_move 2
   :melee_attack_strength 25
   })

(def super_unit 
  {
   :level_life 100
   :range_attack_length 0
   :range_attack_strength 0
   :range_move 3
   :melee_attack_strength 40
   })
  
  
