(ns keys.subject
  "The subject is a representation of an authenticatable in the system.
   An example of an subject is a user, or a client system.

   The subject in keys is represented by a map containing three items:
   {
    :id ; ID of the subject
    :keys ; A collection of keys which specify which permissions this subject has
    :info ; A custom information, this can be anything including nil, and can be used to match the subject
          ; to other parts of the system
   }"
  (:require [slingshot.slingshot :refer :all]))

(def ^:dynamic *current-subject* nil)

(defn subject []
  *current-subject*)

(defn subject-info []
  (get *current-subject* :info nil))

(defn subject-keys []
  (get *current-subject* :keys nil))

(defn valid-subject-map [subject]
  (and (contains? subject :keys)
       (contains? subject :id)
       (contains? subject :info)
       (vector? (:keys subject))))

(defmacro with-subject [subject & body]
  `(let [sbj# ~subject]
    (if (keys.subject/valid-subject-map sbj#)
      (binding [keys.subject/*current-subject* sbj#]
        ~@body)
      (throw+ {:type :invalid-subject :subject sbj#}))))
