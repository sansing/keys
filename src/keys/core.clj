(ns keys.core
  (:require [keys.subject :refer :all]
            [keys.key :refer :all]
            [slingshot.slingshot :refer :all]))

(defn is-allowed?
  "Given a subject checks if the subject is allowed to use a certain key."
  [subject k]
  (let [subjectks (subject-keys subject)]
    (or (nil? k)
        (contains-key? k subjectks))))

(defmacro secured
  "Secures an expression with a given key. The current subject binding requires to have the permission to perform
   the expression.

   Either returns the value of the evaluated body, or throws one of the following slingshot exceptions:
    - {:type :no-subject} ; The current subject has not been set
    - {:type :invalid-key :key <k>} ; The key provided is not a valid key (it is not a vector)
    - {:type :unauthorized :required <k> :subject <current-subject>} ; The current subject is not authorized to perform
   the expression"
  [k & body]
  `(let [k# ~k
         sbj# (subject)]
     (cond
       (nil? sbj#) (throw+ {:type :no-subject})
       (not (valid-key? k#)) (throw+ {:type :invalid-key :key k#})
       (not (is-allowed? sbj# k#)) (throw+ {:type :unauthorized :required k# :subject sbj#})
       :else ~@body)))


(comment
;; Testing out secured macro
(defn make-admin [] {:id 1 :keys [[:user] [:admin]] :info nil})
(defn make-user [] {:id 1 :keys [[:user]] :info nil})

(defn user-fn [one two]
  (secured [:user]
    (println "user only!")
    (+ one two)))

(defn admin-fn [one two]
  (secured [:admin]
    (println "admin only!")
    (* one two)))

(with-subject (make-user)
              (user-fn 1 2))

(with-subject (make-admin)
              (user-fn 1 2))

(with-subject (make-user)
              (admin-fn 1 2))

(with-subject (make-admin)
              (admin-fn 1 2))
)

