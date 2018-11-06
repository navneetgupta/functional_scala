package com.navneetgupta.learning.scalaz

object ApplicativeEx extends App {

  // Applicative
  // trait Applicative[F[_]] extends Apply[F] { self => def point[A](a: => A): F[A] def pure[A](a: => A): F[A] = point(a)}

  // So Applicative extends another typeclass Apply, and introduces point and its alias pure.
  // Functor's map takes a function A => B, What for (A, A ) => B
  val f1: List[Int => Int] = List(1, 2, 3, 4) map {

    (_: Int) * (_: Int)
  }.curried

  println(f1 map (_ (3)))

}
