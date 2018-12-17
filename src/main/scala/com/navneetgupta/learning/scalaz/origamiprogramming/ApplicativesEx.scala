package com.navneetgupta.learning.scalaz.origamiprogramming

import scalaz._, Scalaz._

object ApplicativesEx extends  App {
  // Scalaz implements Monoid[m].applicative to turn any monoids into an ap- plicative.

  println(Monoid[Int].applicative.ap2(1,2)(0))
  println(Monoid[List[Int]].applicative.ap2(List(1,2,3), List(4,5,6,7))(Nil))


  /**
    * Like monads, applicative functors are closed under products; so two independent idiomatic effects can generally be fused into one, their product.
    * In Scalaz, product is implemented under Applicative typeclass
    * */

  println(Applicative[List].product[Option].point(1))

  println(((List(1), 2.some) |@| (List(2,3), 4.some))(_ |+| _ ))
  println(((List(1,2,3,4,5,6), 2.some) |@| (List(7,8,9), 14.some))(_ |+| _ ))

  println(((List(1,2,3,4), 1.success[String]) |@| (List(5,6,7,8), "failed".failure[Int])) (_ |+| _))

  /**
    * Unlike monads in general, applicative functors are also closed un- der composition;
    * so two sequentially-dependent idiomatic effects can generally be fused into one, their composition.
    * */

  println(Applicative[List].compose[Option].point(1))


}

