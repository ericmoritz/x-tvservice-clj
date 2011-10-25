(ns tvservice.models.feed
  (:require [clojure.xml :as xml])
  (:import [java.util.regex Pattern]))


(defn children [e]
  (:content e))

(defn text-node [tag]
  (-> tag children first))


(defn first-child [parent tag]
  "Get the first matching child"
  (first
   (filter #(= tag (:tag %))
           (children parent))))

(defn re-escape [str]
  (Pattern/quote str))

(defn show-names->patterns [show-names]
  (map #(re-pattern (str "(?i)" (re-escape %))) show-names))

(defn show-matches? [show-names item]
  "True if the title of matches one of the show names"
  (let [patterns (show-names->patterns show-names)
        title (text-node (first-child item :title))]
    (some #(re-find % title) patterns)))


(defn keep-channel-child? [show-names child]
  (case (:tag child)
    :item (show-matches? show-names child) ; keep items whose title matches
    true)) ; keep any tag that's not an item


(defn process-channel [show-names channel]
  (assoc channel
    :content
    (filter (partial keep-channel-child? show-names) (children channel))))

(defn process-feed [show-names feed]
  (assoc feed
    :content
    (map (partial process-channel show-names) (children feed))))

(defn load-feed [input]
  (xml/parse input))

(defn filter-feed [input show-names])
