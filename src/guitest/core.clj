(ns guitest.core
  (:use seesaw.core))

(defn -main [& args]
  (let [open-action (action 
		     :handler (fn [e] (alert "I should open a new something."))
		     :name "Open ..."
		     :key  "menu O"
		     :tip  "Open a new something something.")
	exit-action (action 
		     :handler (fn [e] (println "Pushed!"))
		     :name "Exit"
		     :tip  "Close this window")]
    (invoke-later
     (-> (frame :title "Hello"
		:content (border-panel
			  :north (toolbar :items [open-action exit-action])
			  :center "Insert content here")
		:on-close :exit)
	 pack!
	 show!))))

       
