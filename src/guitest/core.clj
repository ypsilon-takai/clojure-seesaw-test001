(ns guitest.core
  (:gen-class)
  (:use [guitest.imagetool]
	[seesaw core chooser]))

(import '(java.awt AWTException Robot Rectangle Toolkit Graphics)
        '(java.awt.image BufferedImage PixelGrabber)
		'(java.awt.geom AffineTransform)
        '(java.io File IOException)
        '(javax.imageio ImageIO))


;;;;;;
;; image datas
(def *src-buffered-image* (atom nil))
(def *dest-buffered-image* (atom nil))
(def *src-img-arr* (atom nil))
(def *img-height* (atom nil))
(def *img-width* (atom nil))

;;;;;
;; select file
(defn new-filename []
  (choose-file :type :open
	       :multi? false
;;	       :selection-mode :files-only
	       :remember-directory? true
	       :filters [["Images" ["png" "jpg"]]]
	       :success-fn (fn [fc file] (.getAbsolutePath file))))

;;;;;
;; file to save
(defn save-file []
  (choose-file :type :save
	       :multi? false
;;	       :selection-mode :files-only
	       :remember-directory? true
		   :filters [["Images" ["png" "jpg"]]]))


;;;;;;
;; create new image datas
(defn read-image [filename]
  "returns BufferedImage of <filename>"
  (ImageIO/read (File. filename)))

(defn create-image-bufs [srcfilename]
  (let [s-image (read-image srcfilename)
	h (.getHeight s-image)
	w (.getWidth s-image)
	d-image (BufferedImage. w h  BufferedImage/TYPE_BYTE_GRAY)
	s-img-array (long-array (* w h))]
    (do 
      (.grabPixels (PixelGrabber. s-image 0 0 w h s-img-array 0 w))
      [s-image w h d-image s-img-array])))


(defn setup-new-image-data! [srcfilename]
  (let [[src w h dest arr] (create-image-bufs srcfilename)]
    (do
      (reset! *src-buffered-image* src)
      (reset! *dest-buffered-image* dest)
      (reset! *src-img-arr* (create-src-img-array arr w h))
      (reset! *img-height* h)
      (reset! *img-width* w))
    [src dest]))
  
(defn select-and-set-to-new-image! [e]
  (let [[in-bi out-bi] (setup-new-image-data! (new-filename))
	src-lbl (select (to-root e) [:#srcimagelabel])
	dest-lbl (select (to-root e) [:#destimagelabel])]
    (do
      (config! src-lbl :icon in-bi)
      (config! dest-lbl :icon out-bi)
      (repaint! src-lbl)
      (repaint! dest-lbl))))


;;;;;;
;; save image data
(defn save-image [im-buf f]
  "save contents of imagebuffer to the file"
  (ImageIO/write im-buf f))

(defn save-image-as-new-file [e]
  (let [f (new-file)]
    (save-image @*dest-buffered-image* f)))


;;;;;;;;;;;;;;
;; image manipulation

(defn create-bufferedimage-from-arr [img-arr]
  (let [w (:width img-arr)
	h (:height img-arr) 
	bi (BufferedImage. w h BufferedImage/TYPE_BYTE_GRAY)
	mis (MemoryImageSource. w h arr 0 w)
	img (.createImage mis)]
	(doto (.createGraphics bi)
		  (.drawImage img (.AffineTransform) null)
		  (.dispose))
    bi))
	
;; old
;; (defn create-grayscaled-img [in-bi out-bi]
;;   (let [w (.getWidth in-bi)
;; 	h (.getHeight in-bi)]
;;     (dorun
;;      (for [x (range w) y (range h)]
;;        (.setRGB out-bi x y (grayscaled-val(.getRGB in-bi x y)))))))


(defn create-grayscaled-img [in-img-arr]
  (create-bufferedimage-from-arr (transform-to-grayscale in-img-arr)))


;; old
;; (defn create-edgedetect-img [in-bi out-bi type]
;;   (let [w (.getWidth in-bi)
;; 	h (.getHeight in-bi)
;; 	[op-list-x op-list-y] (create-operation type)]
;;     (dorun
;;      (for [x (range 1 (dec w)) y (range 1 (dec h))]
;;        (.setRGB out-bi x y
;; 		(monochrome-val
;; 		 (- 0xff (mean
;; 		  (reduce + (map (fn [[[dx dy] val]]
;; 				   (* val (magnitude (.getRGB in-bi (+ x dx) (+ y dy)))))
;; 				 op-list-x))
;; 		  (reduce + (map (fn [[[dx dy] val]]
;; 				   (* val (magnitude (.getRGB in-bi (+ x dx) (+ y dy)))))
;; 				 op-list-y))))))))))


(defn create-edgedetect-img [in-img-arr type]
  (create-bufferedimage-from-arr (transform-to-edge-img in-img-arr type)))

(create-grayscaled-img @*src-buffered-image* @*dest-buffered-image*)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;  GUI stuff

;;;;;;;;;;;;;;
;; menu handler
(def open-action
     (action 
      :handler (fn [e] (select-and-set-to-new-image! e))
      :name "Open ..."
      :key  "menu O"
      :tip  "Open a new something something."))

(def seve-action
     (action
      :handler (fn [e] (save-image-as-new-file e))
      :name "Save ..."
      :key "menu 1"
      :tip "Save created image to the file"))


(def exit-action
     (action 
      :handler (fn [e] (System/exit 0))
      :name "Exit"
      :key "menu 2"
      :tip  "Close this window"))

;; for debug
;; (def exit-action
;;      (action 
;;       :handler (fn [e] (dispose! e))
;;       :name "Exit"
;;       :tip  "Close this window"))

(def grayscale-action
     (action
      :handler (fn [e]
		 (do
		   (create-grayscaled-img @*src-buffered-image* @*dest-buffered-image*)
		   (repaint! (select (to-root e) [:#destimagelabel]))))
      :name "Grayscale"
      :key "menu 4"
      :tip "Create grayscaled image."))


(def detect-edge-roberts-action
     (action
      :handler (fn [e]
		 (do
		   (create-edgedetect-img @*src-buffered-image* @*dest-buffered-image* :roberts)
		   (repaint! (select (to-root e) [:#destimagelabel]))))
      :name "Edge:roberts"
      :tip "Detect edge of left image."))

(def detect-edge-norm-action
     (action
      :handler (fn [e]
		 (do
		   (create-edgedetect-img @*src-buffered-image* @*dest-buffered-image* :norm)
		   (repaint! (select (to-root e) [:#destimagelabel]))))
      :name "Edge:normal"
      :tip "Detect edge of left image."))

(def detect-edge-sobel-action
     (action
      :handler (fn [e]
		 (do
		   (create-edgedetect-img @*src-buffered-image* @*dest-buffered-image* :sobel)
		   (repaint! (select (to-root e) [:#destimagelabel]))))
      :name "Edge:sobel"
      :tip "Detect edge of left image."))



;;;;;;;;;;;;;;
;; main-window
(defn main-window []
  (frame :title "Hello"
	    :width 400
	    :height 400
	    :content (border-panel
		      :north (toolbar :items [open-action
					      grayscale-action
					      detect-edge-norm-action
					      detect-edge-roberts-action
					      detect-edge-sobel-action
					      exit-action])
		      :center (horizontal-panel
			       :items [(label :id :srcimagelabel
					       :icon @*src-buffered-image*
					       :paint nil)
				       (label :id :destimagelabel
					       :icon @*dest-buffered-image*
					       :paint nil)]))
					; should change to :exit when finalize.
	    :on-close :dispose))


;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Main function

;;
;;(def srcfilename '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y01.jpg")
(def startupfilename '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y02.jpg")

(defn -main [& args]
  (native!)
  (setup-new-image-data! startupfilename)
  (let [main (main-window)]
    (invoke-later
     (-> main
	 show!))))

       
