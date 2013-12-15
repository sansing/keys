(ns keys.core-test
  (:require [clojure.test :refer :all]
            [keys.core :refer :all]
            [keys.subject :refer :all]
            [slingshot.slingshot :refer :all])
  (:use midje.sweet))

(deftest is-allowed-test
  (facts "Is allowed should check if a subject is allowed to do a certain action"
    (is-allowed? {:id 1 :keys [[:obj :read 1]] :info nil} [:obj :read 1]) => true
    (is-allowed? {:id 1 :keys [[:obj :read 1] [:obj :update 1]] :info nil} [:obj :read 1]) => true
    (is-allowed? {:id 1 :keys [] :info nil} nil) => true
    (is-allowed? {:id 1 :keys [[:obj :read 1]] :info nil} nil) => true
    (is-allowed? {:id 1 :keys [[:obj :read 1]] :info nil} [:obj :update 1]) => false
    (is-allowed? {:id 1 :keys [] :info nil} [:obj :read 1]) => false))

(defn make-key [] [:obj :update])
(defn make-invalid-key [] {:obj :update})

(deftest secured-test
  (facts "Secured should not evaluate the body when a subject is not allowed to execute the expression"
    ;; without subject
   (try+
     (secured [:obj :read] "is allowed")
     (catch [:type :no-subject] ex
       ex)) => {:type :no-subject}

    ;; Is allowed case
    (with-subject {:id 1 :keys [[:obj :read]] :info nil}
      (secured [:obj :read] "is allowed")) => "is allowed"
    ;; Is not allowed case
    (try+
      (with-subject {:id 1 :keys [[:obj :read]] :info nil}
        (secured [:obj :update] "is not allowed"))
      (catch [:type :unauthorized] {:keys [type required subject] :as ex}
        ex)) => {:type :unauthorized :required [:obj :update] :subject {:id 1 :keys [[:obj :read]] :info nil}}
    ;; Is allowed (with expression)
    (with-subject {:id 1 :keys [[:obj :update] [:obj :read]] :info nil}
      (secured (make-key) "is allowed")) => "is allowed"
    ;; Invalid expression
    (try+
      (with-subject {:id 1 :keys [[:obj :update] [:obj :read]] :info nil}
                    (secured (make-invalid-key) "is allowed"))
      (catch [:type :invalid-key] ex
        ex)) => {:type :invalid-key :key {:obj :update}}))