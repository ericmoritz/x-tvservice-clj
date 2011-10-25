(ns tvservice.test.models.shows
  (:require [tvservice.models.shows :as shows])
  (:require [tvservice.models.base :as base])
  (:use [clojure.test])
  (:import [java.io File]))

(defn shows-fixture [f]
  (shows/drop!)
  (f))

(use-fixtures :each shows-fixture)

(deftest test-shows
  (testing "Blank state"
    (is (= {} (shows/all))))
  (testing "Add show"
    (let [expected {"test" {"slug" "test", "name" "Test"}}]
      (is (= expected (shows/store "test" "Test")))
      (is (= expected (shows/all)))))
  (testing "Update show"
    (let [expected {"test" {"slug" "test", "name" "Test!"}}]
      (is (= expected (shows/store "test" "Test!")))
      (is (= expected (shows/all)))))
  (testing "Remove show"
    (shows/store "test" "Test")
    (shows/delete "test")
    (is (= {} (shows/all)))))
