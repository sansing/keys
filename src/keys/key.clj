(ns keys.key
  "Keys key.core namespace contains functions related to operation of keys. Keys are the
   permission structures."
  )

(defn valid-key?
  "Checks if a key is valid"
  [k]
  (and (not (nil? k))
       (vector? k)
       (not (empty? k))))

(defn key=
  "Compares to keys, checking if they are equal.
   A key is equal if all the items of one key, are also present in the other, independent of the item ordering.
   e.g. [:a 1] [1 :a] are the same keys."
  [kone ktwo]
  (= (into #{} kone) (into #{} ktwo)))

(defn contains-key?
  "Checks if a key is contained in a collection of keys."
  [key coll]
  (boolean (some #(key= key %) coll)))

