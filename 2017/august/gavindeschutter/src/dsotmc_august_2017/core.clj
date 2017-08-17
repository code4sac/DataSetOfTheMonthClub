(ns dsotmc-august-2017.core
  (:require [csv-map.core :refer [parse-csv]]))

(def parking-map
  {:package-type "pkgtype"
   :prefix "prefix"
   :timeframe "pkgenday"
   :suffix "suffix"
   :lat "Y"
   :lng "﻿X"
   :aorp "aorp"
   :evtarea "evtarea"
   :address "address"
   :begin-at "enbegin"
   :permit-area "permitarea"
   :parkmob "parkmob"
   :end-at "enend"
   :id "objectid"
   :object-code "obj_code"
   :max-rate "maxrate"
   :gis-object-id "gisobjid"
   :street "street"
   :timelimit "timelimit"})

(defn- build-object [row map]
  (reduce (fn [a [k v]] (assoc a k (get row v)))
          {}
          map))


(defn build-parking [row]
  (build-object row parking-map))


(def objs (map build-parking
               (-> (slurp "ONSTREETPARKING.csv") parse-csv)))