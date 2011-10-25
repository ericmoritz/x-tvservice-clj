(ns tvservice.models.base
  (:require [clj-json.core :as json])
  (:import [java.io File]))


(defn file-exists? [path]
  (.exists (File. path)))

(defn drop-data [path]
  (if (file-exists? path)
    (.delete (File. path))))

(defn read-data [path default]
  (if (file-exists? path)
    (json/parse-string (slurp path))
    default))


(defn store-data [path data]
  (spit path (json/generate-string data ))
  data)

