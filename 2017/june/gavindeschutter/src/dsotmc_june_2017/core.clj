(ns dsotmc-june-2017.core
  (:require [csv-map.core :refer [parse-csv]]))

(def crime-map
  {:id "OBJECTID"
   :offense-code "Offense_Code"
   :beat "Beat"
   :grid "Grid"
   :offense-ext "Offense_Ext"
   :description "Description"
   :offense-category "Offense_Category"
   :occurence-date "Occurence_Date"
   :occurence-time "Occurence_Time"
   :record-id "Record_ID"
   :police-district "Police_District"})


(defn- build-object [row map]
  (reduce (fn [a [k v]] (assoc a k (get row v)))
          {}
          map))


(defn build-crime [row]
  (build-object row crime-map))


(def objs (map build-crime
               (-> (slurp "sacramento_crime_stats.csv") parse-csv)))
