(ns keys.core
  (:require [keys.subject :refer :all]
            [keys.key :refer :all]
            [slingshot.slingshot :refer :all]))

(defn is-allowed? [subject k]
  (let [subjectks (subject-keys subject)]
    (if (nil? k)
      true
      (contains-key? k subjectks))))

(defmacro secured [k & body]
  `(let [k# ~k
         sbj# (keys.subject/subject)]
     (when (nil? sbj#)
       (throw+ {:type :no-subject}))
     (when (not (keys.key/valid-key? k#))
       (throw+ {:type :invalid-key :key k#}))
     (when (not (keys.core/is-allowed? sbj# k#))
       (throw+ {:type :unauthorized :required k# :subject sbj#}))
      ~@body))


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

