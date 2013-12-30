(ns keys.key_test
  (:require [clojure.test :refer :all]
            [keys.key :refer :all])
  (:use midje.sweet))

(deftest key-eq-tests
    (facts "Comparing different keys, using key="
      (key= [:obj :read] [:read :obj])   => true ;Keys should not depend on order of the items
      (key= [:obj :read] [:obj :read])   => true
      (key= [:obj :read] [:obj :read 1]) => false
      (key= nil [:obj :read])            => false
      (key= [:obj :read] nil)            => false))

(deftest contains-key-tests
  (facts "Testing contains-key"
    (contains-key? [:obj :read] [[:obj :read] [:obj :write]])   => true
    (contains-key? [:obj :update] [[:obj :read] [:obj :write]]) => false
    (contains-key? nil nil)                                     => false
    (contains-key? nil [[:obj :read]])                          => false
    (contains-key? [:obj :read] nil)                            => false))

(deftest valid-key-tests
  (facts "Testing valid-key?"
   (valid-key? [:obj]) => true
   (valid-key? [:obj :read]) => true
   (valid-key? []) => false
   (valid-key? nil) => false
   (valid-key? {:obj :read}) => false))
