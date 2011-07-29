(ns guitest.core2
  (:use seesaw.core))

(import '(java.awt AWTException Robot Rectangle Toolkit)
        '(java.awt.image BufferedImage)
        '(java.io File IOException)
        '(javax.imageio ImageIO))

;; info
;;
;;   test image D:\Profiles\q3197c\workspace\clojure\guitest\res\y.jpg
;;
(def srcfilename '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y02.jpg")


;;;;
(defn get-image [filename]
  "returns BufferedImage of <filename>"
  (ImageIO/read (File. filename)))


(defn create-image-bufs [srcfilename]
  (let [s-image (get-image srcfilename)
	h (.getHeight s-image)
	w (.getWidth s-image)
	d-image (BufferedImage. w h  BufferedImage/TYPE_BYTE_GRAY)]
    [s-image d-image]))

(defn create-grayscaled-img [in-bi out-bi]
  (let [w (.getWidth in-bi)
	h (.getHeight in-bi)]
    (dorun
    (for [x (range w) y (range h)]
      (.setRGB out-bi x y (grayscaled-val(.getRGB in-bi x y)))))))

(defn set-new-image []
  (let [filename (choose-file :type :open
			      :multi? false
			      :selection-mode :files-only
			      :remember-directory? true
			      :filters [["Images" ["png" "jpeg"]]]
			      :success-fn (fn [fc file] (.getAbsolutePath file)))
	src-image (ImageIO/read (File. filename))]
	
    (do
      (config! (select 

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

(def detect-edge-action
     (action
      :handler (fn [e] (alert "now not here."))
      :name "Edge detection"
      :tip "Detect edge of left image."))


;(get-image '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y.jpg")

(repaint! (select [:*] root)

(defn main-window []

  (frame :title "Hello"
	    :width 400
	    :height 400
	    :content (border-panel
		      :north (toolbar :items [open-action detect-edge-action exit-action])
		      :center (horizontal-panel
			       :items [(label :id :srcimagelabel
					       :icon (src-image)
					       :paint nil)
				       (label :id :destimagelabel
					       :icon (dest-image)
					       :paint nil)]))
					; should change to :exit when finalize.
	    :on-close :dispose))

(defn -main [& args]
  (let [main (main-window)]
    (invoke-later
     (-> main
	 show!)))

       
