(ns guitest.core2
  (:use seesaw.core))

(def open-action
     (action 
      :handler (fn [e] (alert "I should open a new something."))
      :name "Open ..."
      :key  "menu O"
      :tip  "Open a new something something."))

(def exit-action
     (action 
      :handler (fn [e] (System/exit 0))
      :name "Exit"
      :tip  "Close this window"))

(defn main-window []
     (frame :title "Hello"
	    :width 200
	    :height 200
	    :content (border-panel
		      :north (toolbar :items [open-action exit-action])
		      :center (horizontal-panel
			       :items [(canvas :id :srcimage
					       :background "#BBBBDD"
					       :paint nil)
				       (canvas :id :destimage
					       :background "#222233"
					       :paint nil)]))
	    :on-close :exit))
     
(defn -main [& args]
    (invoke-later
     (-> (main-window)
;	 pack!
	 show!)))

       
;;;;;;;;;;;;;;;;;;;
;; funcs

(defn test-frame []
     (frame :title "test"
	    :width 200
	    :height 200
	    :content (label :id :srcimage
			     :icon (clojure.java.io/resource "seesaw/examples/rss.gif"))

	    :on-close :exit))
  

(show! (test-frame))
