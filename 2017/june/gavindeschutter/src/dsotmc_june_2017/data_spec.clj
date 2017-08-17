(ns dsotmc-june-2017.data-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::non-empty-string (s/and string? not-empty))


(s/def ::police-district #{"1" "2" "3" "4" "5" "6"})


(s/def ::beat #{"1A" "1B" "1C"
                "2A" "2B" "2C"
                "3A" "3B" "3M"
                "4A" "4B" "4C"
                "5A" "5B" "5C"
                "6A" "6B" "6C" "6D" "6E"})

(defn grid-gen []
  (gen/fmap (fn [_] (let [n (-> (range 101 1662) rand-nth str)]
                      (if (= (count n) 4)
                        (str n)
                        (str "0" n))))
            (gen/any)))

(s/def ::grid (s/with-gen any?
                          grid-gen))

(s/def ::crime (s/keys :req-un [::beat
                                ::grid
                                ::police-district]))