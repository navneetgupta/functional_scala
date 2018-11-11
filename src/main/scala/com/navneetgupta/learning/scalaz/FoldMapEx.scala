package com.navneetgupta.learning.scalaz

import scalaz._, Scalaz._

object FoldMapEx extends App {
  /*trait Foldable[F[_]] { self =>

    /** Map each element of the structure to a [[scalaz.Monoid]], and combine the results. */
    def foldMap[A,B](fa: F[A])(f: A => B)(implicit F: Monoid[B]): B

    /**Right-associative fold of a structure. */
    def foldRight[A, B](fa: F[A], z: => B)(f: (A, => B) => B): B ...
  }
*/

  // foldMap states that it should have an implicit Monoid instance for B

  assert((List(1, 2, 3) foldMap {
    identity
  }) == 6)

  assert((List(true, false, true, true) foldMap {
    Tags.Disjunction.apply
  }) == Tags.Disjunction(true))


}
