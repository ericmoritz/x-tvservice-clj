(ns tvservice.models.shows
  (:require [tvservice.models.base :as base]))

; Read in the config values
(def environ (System/getenv))
(def db-root (get environ "DB_ROOT" "/tmp"))
(def shows-path (str db-root "/shows.json"))

(defn drop! []
  (base/drop-data shows-path))

(defn all []
  (base/read-data shows-path {}))

(defn store [slug name]
  (base/store-data shows-path (assoc (all) slug {"slug" slug "name" name})))

(defn delete [slug]
  (base/store-data shows-path (dissoc (all) slug)))

