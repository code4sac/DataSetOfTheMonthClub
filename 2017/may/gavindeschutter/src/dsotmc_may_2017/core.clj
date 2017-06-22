(ns dsotmc-may-2017.core
  (:require [csv-map.core :refer [parse-csv]]
            [clojure.xml]
            [hickory.core :as hickory]
            [hickory.select :as hs]))

(def direction-map
  {:lat "Y"
   :lng "﻿X"})


(def pole-map
  {:type "POLE_TYPE"
   :id "GISOBJID"
   :installation "INSTL_TYPE"
   :status "OBJ_STATUS"
   :comment "FIELD_COMMENT"})


(defn- build-object [row map]
  (reduce (fn [a [k v]] (assoc a k (get row v)))
          {}
          map))


(defn retrieve-sign [row x]
  (let [dir (str "SIGN_DIR" x)
        face (str "SIGN_FACE" x)]
    {x {:dir (get row dir)
        :face (get row face)}}))


(defn- remove-blank-signs [m]
  (remove (fn [[_ {:keys [dir face]}]]
            (every? clojure.string/blank? [dir face]))
          m))


(defn- retrieve-signs [row]
  (->> (for [x (range 1 6)] (retrieve-sign row x))
       (apply merge)
       remove-blank-signs
       (into {})))


(defn build-sign [row]
  (let [pole (build-object row pole-map)
        direction (build-object row direction-map)
        signs (retrieve-signs row)]
    (for [[pos {:keys [dir face]}] signs]
      (assoc direction :id (str (-> pole :id) "-" pos)
                       :pole pole
                       :position pos
                       :direction dir
                       :face face))))


(def objs (mapcat build-sign
                  (-> (slurp "Signs.csv") parse-csv)))

