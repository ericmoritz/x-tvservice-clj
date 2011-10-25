(ns tvservice.test.models.feed
  (:require [tvservice.models.feed :as feed]
            [tvservice.models.shows :as shows])
  (:require [clojure.xml :as xml])
  (:use clojure.test)
  (:use clojure.pprint))


(defn shows-fixture [f]
  (shows/drop!)
  (f))

(use-fixtures :each shows-fixture)

(def fixture {:tag :rss
              :content [{:tag :channel
                         :content [{:tag :title
                                    :content ["a rss feed"]}
                                   {:tag :item
                                    :content [{:tag :title
                                               :content ["This is a Test"]}]}
                                   {:tag :item
                                    :content [{:tag :title
                                               :content ["No Match"]}]}]}]})
(def expected {:tag :rss
              :content [{:tag :channel
                         :content [{:tag :title
                                    :content ["a rss feed"]}
                                   {:tag :item
                                    :content [{:tag :title
                                               :content ["This is a Test"]}]}]}]})


(def show-names ["Test" "Show"])


(deftest test-text-node
  (let [e (-> fixture :content ; <rss>
              first :content   ; <channel>
              first)]          ; <title>
    (is (= "a rss feed" (feed/text-node e)))))


(deftest test-first-child
  (let [channel (-> fixture :content first)
        item (-> channel :content (get 1))] 
    (is (= item (feed/first-child channel :item)))))


(deftest test-re-escape
  (is (not (re-find (re-pattern (feed/re-escape "test.txt")) "test-txt")))
  (is (re-find (re-pattern (feed/re-escape "test.txt")) "test.txt")))


(deftest test-show-names->patterns
  (is (some #(re-find % "test") (feed/show-names->patterns show-names)))
  (is (not (some #(re-find % "foo") (feed/show-names->patterns show-names)))))


(deftest show-matches?
  (testing "Match"
    (let [channel (-> fixture :content first)
          item (-> channel :content (get 1))] 
      (is (feed/show-matches? show-names item))))
  (testing "Not Match"
    (let [channel (-> fixture :content first)
          item (-> channel :content (get 2))] 
      (is (not (feed/show-matches? show-names item))))))


(deftest keep-channel-child?
  (testing "Title Match"
    (let [channel (-> fixture :content first)
          item (-> channel :content (get 0))] 
      (is (feed/keep-channel-child? show-names item))))
  (testing "Item Match"
    (let [channel (-> fixture :content first)
          item (-> channel :content (get 1))] 
      (is (feed/keep-channel-child? show-names item))))
  (testing "Item Not Match"
    (let [channel (-> fixture :content first)
          item (-> channel :content (get 2))] 
      (is (not (feed/keep-channel-child? show-names item))))))


(deftest process-channel
  (let [channel (-> fixture :content first)
        expected (-> expected :content first)
        result (feed/process-channel show-names channel)]
    (is (= expected result))))

(deftest process-feed
  (let [result (feed/process-feed show-names fixture)]
    (is (= expected result))))
