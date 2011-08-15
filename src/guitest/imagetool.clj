(ns guitest.imagetool
  (:use (clojure.contrib.math)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; rgb tool
(defn rgba-to-list [rgb-val]
  (let [a (/ (bit-and rgb-val 0xff000000) 0x1000000)
	r (/ (bit-and rgb-val 0xff0000) 0x10000)
	g (/ (bit-and rgb-val 0xff00) 0x100)
	b (bit-and rgb-val 0xff)]
    [a r g b]))
  

(defn list-to-rgba [r g b a]
  ([r g b]
     (list-to-rgba [r g b 0xFF]))
  ([r g b a]
     (+ (* 0x1000000 a)
	(* 0x10000 r)
	(* 0x100 g)
	(* 0x1 b)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; binarize and grayscale
(defn- magnitude [rgb]
  (let [[_ r g b] (rgba-to-list rgb)]
    (bit-shift-right (+ (* 77 r)
			(* 150 g)
			(* 29 b))
		     8)))

(defn- binarize [mag th]
  (if (>= mag th)
    0xFF
    0))

(defn- grayscaled-val [rgb]
  (let [mag (magnitude rgb)]
    (rgb-val mag mag mag)))

(defn- binarized-val
  ([rgb]
     (binarized-val rgb 128))
  ([rgb th]
     (let [mag (binalize (magnitude rgb) th)]
       (list-to-rgba mag mag mag)))


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
  (sqrt (/ (+ (expt x 2)
	      (expt y 2))
	   2)))

(defn mean [x y]
  (/ (+ x y) 2))


(defn create-operation [type]
  (let [x-op (type x-operator)
	y-op (type y-operator)
	get-val (fn [op x y] (nth (nth op x) y))]
    [(for [a (range 3) b (range 3) :when (not= (get-val x-op a b) 0)]
       [[(+ a -1) (+ b -1)] (get-val x-op a b)])
     (for [a (range 3) b (range 3) :when (not= (get-val y-op a b) 0)]
       [[(+ a -1) (+ b -1)] (get-val y-op a b)])]))
