(ns tvservice.views.shows
  (:require [tvservice.views.common :as common]
            [noir.response :as response] 
            [noir.request :as request] 
            [clj-json.core :as json]
            [tvservice.models.shows :as shows])
  (:use noir.core)
  (:use [noir.request :only [ring-request]]))


(defn txt-show-put [slug txt])


(defn read-body [req]
  (let [body (:body req)]
    (slurp body)))


(defn http-415 [msg]
  {:status 415
   :body msg})

(defn http-404 [msg]
  {:status 404
   :body msg})

(defn http-200 [msg]
  {:status 200
   :body msg})

(defn render-show [show]
  (-> show
      (assoc "id" (str "/shows/" (show "slug")))))

(defn put-txt-show [req slug]
  (shows/store slug (read-body req))
  (response/json ((map render-show (shows/all)) slug)))


(defpage "/shows/" []
  (response/json {"members" (map render-show (vals (shows/all)))}))

(defpage [:put "/shows/:slug"] {:keys [slug]}
  (let [req (ring-request)]
    (case (:content-type req)
      "text/plain"  (put-txt-show req slug)
      (http-415 "use: text/plain"))))


(defpage [:get "/shows/:slug"] {:keys [slug]}
  (if-let [show ((shows/all) slug)]
    (response/json (render-show show))
    (http-404 (str slug " Not Found"))))


(defpage [:delete "/shows/:slug"] {:keys [slug]}
  (if-let [show ((shows/all) slug)]
    (do (shows/delete slug)
        (http-200 (str slug " Deleted")))
    
    (http-404 (str slug " Not Found"))))


