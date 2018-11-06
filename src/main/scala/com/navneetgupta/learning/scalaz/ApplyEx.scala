package com.navneetgupta.learning.scalaz

import scalaz.Scalaz._
import scalaz._

object ApplyEx extends App {
  /**
    * trait Apply[F[_]] extends Functor[F] { self =>
    * def ap[A,B](fa: => F[A])(f: => F[A => B]): F[B]
    * }
    **/

  // Using ap, Apply enables <*>, *>, and <* operator.

  println(9.some <*> ((_: Int) * 2).some)

  // *> and <* are variations that returns only the rhs or lhs.

  println(none *> 2.some) // None
  println(none <* 2.some) // None
  println(1.some <* 2.some) // Some(1)
  println(1.some *> 2.some) // Some(2)


  println(^(3.some, 5.some) {
    _ + _
  }) // Some(8)

  println(3.some <*> ((_: Int) + 5).some)

  // This is actually useful because for one-function case, we no longer need to put it into the container.
  // The new ˆ(f1, f2) {...} style is not without the problem though. It doesn’t seem to handle Applicatives that takes two
  // type parameters like Function1, Writer, and Validation. There’s another way called Applicative Builder,
  // which apparently was the way it worked

  println((3.some |@| 5.some) {
    _ + _
  }) // Some(8)

  println(List(1, 2, 3) <*> List(((_: Int) * 0), ((_: Int) + 100), (x: Int) => x * x))
  println(^(List(1, 2, 3), List(5, 6, 7)) {
    _ + _
  })

  println(List(1, 2, 3) <*> List((_: Int) + 5, (_: Int) + 6, (_: Int) + 7))
  println((List("he", "hi", "hello") |@| List("?", "!", "@")) {
    _ + _
  })
}
