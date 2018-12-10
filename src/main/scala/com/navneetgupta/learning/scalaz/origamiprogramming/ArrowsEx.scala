package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._, Scalaz._

object ArrowsEx extends App {
  val f = (_ : Int) + 1
  val g = ( _ : Int) * 100

  println((f *** g) (1,2)) // (2,200) // (***) combines two arrows into a new arrow by running the two arrows on a pair of values
  // (one arrow on the first item of the pair and one arrow on the second item of the pair).

  println((f &&& g) (2))// (3,200) // (&&&) combines two arrows into a new arrow by running the two arrows on the same value:


  // Arrows I think can be useful if you need to add some context to functions and  pairs.

}
