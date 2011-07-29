(ns guitest.imagetooln
  (:use seesaw.core))


(defn magnitude [rgb]
  (let [r (/ (bit-and rgb 0xff0000) 0x10000)
	g (/ (bit-and rgb 0xff00) 0x100)
	b (bit-and rgb 0xff)]
    (bit-shift-right (+ (* 77 r)
			(* 150 g)
			(* 29 b))
		     8)))
    
(defn rgb-val [a r g b]  
  (+ (* 0x1000000 a)
     (* 0x10000 r)
     (* 0x100 g)
     (* 0x1 b)))


(defn grayscaled-val [rgb]
  (let (mag (magnitude rgb)]
  (rgb-val 0xff mag mag mag)))



