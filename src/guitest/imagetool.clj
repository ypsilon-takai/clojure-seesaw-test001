(ns guitest.imagetool
  (:use clojure.contrib.math))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; rgb tool
(defn rgba-to-list [rgb-val]
  (let [a (/ (bit-and rgb-val 0xff000000) 0x1000000)
	r (/ (bit-and rgb-val 0xff0000) 0x10000)
	g (/ (bit-and rgb-val 0xff00) 0x100)
	b (bit-and rgb-val 0xff)]
    [r g b a]))
  

(defn list-to-rgba [rgba-list]
  (let [[r g b a] rgba-list]
    (+ (* 0x1000000 a)
       (* 0x10000 r)
       (* 0x100 g)
       (* 0x1 b))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Magnitude of the data
(defn magnitude [rgba]
  (let [[r g b _] (rgba-to-list rgba)]
    (bit-shift-right (+ (* 77 r)
			(* 150 g)
			(* 29 b))
		     8)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; binarize and grayscale
(defn- binarize [mag th]
  (if (>= mag th)
    0xFF
    0))

(defn monochrome-val [mag]
  (list-to-rgba [mag mag mag 0xff]))

(defn grayscaled-val [rgb]
  (monochrome-val (magnitude rgb)))


(defn binarized-val
  ([rgb]
     (binarized-val rgb 128))
  ([rgb th]
     (let [mag (binarize (magnitude rgb) th)]
       (list-to-rgba [mag mag mag 0xFF]))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; edge detection function

(def x-operator {:norm [[0 0 0]
			[0 1 -1]
			[0 0 0]]

		 :roberts [[0 0 0]
			   [0 1 0]
			   [0 0 -1]]
		 :sobel [[-1 0 1]
			 [-2 0 2]
			 [-1 0 1]]})

(def y-operator {:norm [[0 0 0]
			[0 1 0]
			[0 -1 0]]

		 :roberts [[0 0 0]
			   [0 0 1]
			   [0 -1 0]]
		 :sobel [[-1 -2 -1]
			 [0 0 0]
			 [1 2 1]]})

(defn rms [x y]
  (int
   (sqrt (/ (+ (expt x 2)
	       (expt y 2))
	    2))))

(defn mean [x y]
  (int (abs (/ (+ x y) 2))))


(defn create-operation [type]
  (let [x-op (type x-operator)
	y-op (type y-operator)
	get-val (fn [op x y] (nth (nth op x) y))]
    [(for [a (range 3) b (range 3) :when (not= (get-val x-op a b) 0)]
       [[(+ a -1) (+ b -1)] (get-val x-op a b)])
     (for [a (range 3) b (range 3) :when (not= (get-val y-op a b) 0)]
       [[(+ a -1) (+ b -1)] (get-val y-op a b)])]))
