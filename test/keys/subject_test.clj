(ns keys.subject-test
  (:import (clojure.lang ExceptionInfo))
  (:require [clojure.test :refer :all]
            [keys.subject :refer :all]
            [slingshot.slingshot :refer :all])
  (:use midje.sweet))

(deftest subject-test
  (facts "Subject should return subject map, or nil, depending on the binding"
    (binding [keys.subject/*current-subject* {:id 1 :keys [] :info nil}]
      (subject)) => {:id 1 :keys [] :info nil}
    (subject) => nil))

(deftest subject-info-test
  (facts "subject-info should return info part of the current subject"
    (subject-info) => nil
    (binding [keys.subject/*current-subject* {:id 1 :keys [[:obj :read 1]] :info {:name "john"}}]
      (subject-info)) => {:name "john"}
    (subject-info {:id 1 :info {:name "john"} :keys []}) => {:name "john"}
    (subject-info nil) => nil
    (subject-info {:id 1 :keys []}) => nil
    (subject-info {:id 1 :keys [] :info nil})) => nil)

(deftest subject-keys-test
  (facts "subject-keys should return the keys of the current subject"
    (subject-keys) => nil
    (binding [keys.subject/*current-subject* {:id 1 :keys [[:obj :read 1]] :info {:name "john"}}]
      (subject-keys)) => [[:obj :read 1]]
    (subject-keys {:id 1 :info {:name "john"} :keys [[:obj :read]]}) => [[:obj :read]]
    (subject-keys {:id 1 :info {:name "john"} :keys []}) => []
    (subject-keys {:id 1 :info {:name "john"}}) => nil
    (subject-keys {:id 1 :info {:name "john"} :keys []}) => []))

(deftest valid-subject-map-test
  (facts "valid-subject-map should return if the subject map is valid"
    (valid-subject-map {:id 1 :keys [] :info nil}) => true
    (valid-subject-map {:id 1 :keys {} :info nil}) => false
    (valid-subject-map {:id 1 :info nil}) => false
    (valid-subject-map nil) => false))

(deftest with-subject-test
  (facts "The with-subject macro should set the *current-subject* binding,
          and check if the subject map is correct"
    (with-subject {:id 1 :keys [] :info nil}
      *current-subject*) => {:id 1 :keys [] :info nil}
    (try+ (with-subject {:invalid "map"}
            *current-subject*)
          (catch [:type :invalid-subject] {:keys [type subject]}
            subject)) => {:invalid "map"}
    (try+ (with-subject nil
            *current-subject*)
          (catch [:type :invalid-subject] {:keys [type subject]}
            subject)) => nil))

(deftest subject-binding-test
  (facts "with-subject should bind the current subject, so it can be used by the subject function"
    (with-subject {:id 1 :keys [] :info nil}
      (subject)) => {:id 1 :keys [] :info nil}
    (with-subject {:id 1 :keys [[:obj :read]] :info nil}
      (subject-keys)) => [[:obj :read]]))