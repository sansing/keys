(ns keys.subject
  "The subject is a representation of an authenticatable in the system.
   An example of an subject is a user, or a client system.

   The subject in keys is represented by a map containing three items:
   {
    :id   ; ID of the subject
    :keys ; A collection of keys which specify which permissions this subject has
    :info ; Custom information, this can be anything including nil
   }"
  (:require [slingshot.slingshot :refer :all]))

(def ^:dynamic *current-subject* nil)

(defn subject
  "Get the current subject from the context, returns a valid subject map or nil"
  []
  *current-subject*)

(defn subject-info
  "Gets the info part of the current subject from the context, or returns nil"
  []
  (get *current-subject* :info nil))

(defn subject-keys
  "Gets the keys of the current subject from the context"
  []
  (get *current-subject* :keys nil))

(defn valid-subject-map
  "Checks if the provided subject map is valid. Subject maps need to fulfill the following shape:
   {:id <anything> :keys [] :info <anything>}"
  [subject]
  (and (contains? subject :keys)
       (contains? subject :id)
       (contains? subject :info)
       (vector? (:keys subject))))

(defmacro with-subject
  "Sets the current subject context with the subject parameter.
   Only evaluates the body if the subject is a valid subject map and throws an slingshot exception otherwise.
   The slingshot exception contains the following information:
   {:type :invalid-subject :subject <provided-subject>}"
  [subject & body]
  `(let [sbj# ~subject]
    (if (keys.subject/valid-subject-map sbj#)
      (binding [keys.subject/*current-subject* sbj#]
        ~@body)
      (throw+ {:type :invalid-subject :subject sbj#}))))
