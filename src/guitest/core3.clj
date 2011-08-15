(ns guitest.core3
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

(defn get-image [filename]
  "returns BufferedImage of <filename>"
  (ImageIO/read (File. filename)))


;;;;
;; image store
(let [s-image (get-image srcfilename)
      h (.getHeight s-image)
      w (.getWidth s-image)
      d-image (BufferedImage. w h  BufferedImage/TYPE_BYTE_GRAY)]
  (defn set-color-at [x y rgb]
    (.setRGB d-image x y rgb))

  (defn fill-color [rgb]
    (dorun (for [x (range w) y (range h)]
	     (set-color-at x y rgb))))
  
  (defn src-image []
    s-image)

  (defn dest-image []
    d-image))

(defn magnitude [rgb]
  (let [r (/ (bit-and rgb 0xff0000) 0x10000)
	g (/ (bit-and rgb 0xff00) 0x100)
	b (bit-and rgb 0xff)]
    (int (/ (+ (* 77 r)
	       (* 150 g)
	       (* 29 b))
	    256))))
    
(defn grayscaled-val [mag]
  (+ 0xff000000
     (* mag 0x10000)
     (* mag 0x100)
     (* mag 0x1)))

(defn create-grayscaled-img [in-bi out-bi]
  (let [w (.getWidth in-bi)
	h (.getHeight in-bi)]
    (dorun
    (for [x (range w) y (range h)]
      (.setRGB out-bi x y (grayscaled-val
			   (magnitude (.getRGB in-bi x y))))))))

(create-grayscaled-img (src-image) (dest-image))

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
      :handler (fn [e] (println "hey"))
      :name "Edge detection"
      :tip "Detect edge of left image."))


;;(get-image '"D:/Profiles/q3197c/workspace/clojure/guitest/res/y.jpg")

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

	    :on-close :dispose))
  

;;(show! (test-frame))
