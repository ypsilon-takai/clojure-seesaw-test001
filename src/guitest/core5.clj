(ns guitest.core5
  (:use (guitest.imagetool seesaw.core)))

(import '(java.awt AWTException Robot Rectangle Toolkit)
        '(java.awt.image BufferedImage)
        '(java.io File IOException)
        '(javax.imageio ImageIO))

;; info
;;
;;   test image D:\Profiles\q3197c\workspace\clojure\guitest\res\y.jpg
;;
(def srcfilename '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y02.jpg")

;;;;;;
;;
(def src-buffered-image (atom nil))
(def dest-buffered-image (atom nil))

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

(let [[src-init dest-init] (create-image-bufs srcfilename)]
  (reset! src-buffered-image src-init)
  (reset! dest-buffered-image dest-init))


;;;;;;;;;;;;;;
;; image manu
(defn create-grayscaled-img [in-bi out-bi]
  (let [w (.getWidth in-bi)
	h (.getHeight in-bi)]
    (dorun
    (for [x (range w) y (range h)]
      (.setRGB out-bi x y (grayscaled-val(.getRGB in-bi x y)))))))

(defn create-edgedetect-img [in-bi out-bi type]
  (let [w (.getWidth in-bi)
	h (.getHeight in-bi)
	[op-list-x op-list-y] (create-operation type)]
    (dorun
     (for [x (range 1 (dec w)) y (range 1 (dec h))]
       (.setRGB out-bi x y
		(mean 
		 (reduce + (map (fn [[[tx ty] val]]
				  (* (.getRGB in-bi tx ty) val))
				op-list-x))
		 (reduce + (map (fn [[[tx ty] val]]
				  (* (.getRGB in-bi tx ty) val))
				op-list-y))))))))

(create-grayscaled-img @src-buffered-image @dest-buffered-image)

;;;;;;;;;;;;;;
;; 
(defn new-images []
  (let [filename (choose-file :type :open
			      :multi? false
			      :selection-mode :files-only
			      :remember-directory? true
			      :filters [["Images" ["png" "jpeg"]]]
			      :success-fn (fn [fc file] (.getAbsolutePath file)))]
	(create-image-bufs filename)))


(defn reset-images [e]
  (let [[in-bi out-bi] (new-images)
	src-lbl (select (to-root e) [:#srcimagelabel])
	dest-lbl (select (to-root e) [:#destimagelabel])]
    (do
      (reset! src-buffered-image in-bi)
      (reset! dest-buffered-image out-bi)
      (config! src-lbl :icon in-bi)
      (config! dest-lbl :icon out-bi)
      (repaint! dest-lbl)
      (repaint! scr-lbl))))
  

;;;;;;;;;;;;;;
;; handler
(def open-action
     (action 
      :handler (fn [e] (rest-images e))
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
      :handler (fn [e]
		 (do
		   (create-edgedetect-img @src-buffered-image @dest-buffered-image)
		   (repaint! (select (to-root e) [:#destimagelabel]))))
      :name "Edge detection"
      :tip "Detect edge of left image."))


(defn main-window []
  (frame :title "Hello"
	    :width 400
	    :height 400
	    :content (border-panel
		      :north (toolbar :items [open-action detect-edge-action exit-action])
		      :center (horizontal-panel
			       :items [(label :id :srcimagelabel
					       :icon @src-buffered-image
					       :paint nil)
				       (label :id :destimagelabel
					       :icon @dest-buffered-image
					       :paint nil)]))
					; should change to :exit when finalize.
	    :on-close :dispose))

(defn -main [& args]
  (let [main (main-window)]
    (invoke-later
     (-> main
	 show!)))

       
