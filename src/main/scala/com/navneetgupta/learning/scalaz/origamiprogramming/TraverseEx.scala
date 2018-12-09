package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._, Scalaz._

object TraverseEx extends App {

  println(List(1,2,3) traverse { x => (x > 0) option (x+1)})

  println(List(1,2,3).traverse { x => if (x > 0) Some(x+1) else None })

  println(Monoid[Int].applicative.traverse(List(1,2,3)) {_ + 1})

}
