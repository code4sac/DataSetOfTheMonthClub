(ns dsotmc-may-2017.core
  (:require [csv-map.core :refer [parse-csv]]
            [clojure.xml]
            [hickory.core :as hickory]
            [hickory.select :as hs]))

(defn extract-table [row]
  (->> (get row "Description")
        hickory/parse
       hickory/as-hickory
       (hs/select (hs/child (hs/tag :td)))
       (drop 2)))

(defn partition-field-with-value [elements]
  (partition 2 elements))

(defn get-field [e]
  (-> e :content first))

(defn sanitize-value [v]
  (if (= v "<Null>")
    nil
    v))

(defn link-kv [p]
  (let [[k v] (map get-field p)]
    (assoc {} k (sanitize-value v))))

(defn build-object [row]
  (->> (extract-table row)
       partition-field-with-value
       (map link-kv)
       (apply merge)
       (merge (dissoc row "Description"))))

(def objs (map build-object
               (-> (slurp "street_signs.csv") parse-csv)))
