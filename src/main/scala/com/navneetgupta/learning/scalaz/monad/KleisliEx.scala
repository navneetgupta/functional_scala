package com.navneetgupta.learning.scalaz.monad

import scalaz._, Scalaz._

object KleisliEx extends App {
  // When we were learning about the monad laws, we said that the <=< function is just like composition,
  // only instead of working for normal functions like a -> b, it works for monadic functions like a -> m b.

  //  In Scalaz there’s a special wrapper for function of type A => M[B] called Kleisli:
//  sealed trait Kleisli[M[+ _], -A, +B] { self =>
//    def run(a: A): M[B]
//
//    /** alias for `andThen` */
//    def >=>[C](k: Kleisli[M, B, C])(implicit b: Bind[M]): Kleisli[M, A, C] =
//      kleisli((a: A) => b.bind(this(a))(k(_)))
//    def andThen[C](k: Kleisli[M, B, C])(implicit b: Bind[M]): Kleisli[M, A, C] =
//      this >=> k
//
//    /** alias for `compose` */
//    def <=<[C](k: Kleisli[M, C, A])(implicit b: Bind[M]): Kleisli[M, C, B] =
//      k >=> this
//    def compose[C](k: Kleisli[M, C, A])(implicit b: Bind[M]): Kleisli[M, C, B] =
//      k >=> this
//  }
//
//  object Kleisli extends KleisliFunctions with KleisliInstances {
//    def apply[M[+ _], A, B](f: A => M[B]): Kleisli[M, A, B] = kleisli(f)
//  }

  val f = Kleisli { (x: Int) =>
    (x + 1).some
  }
  val g = Kleisli { (x: Int) =>
    (x * 100).some
  }

  val composed = f <=< g
  println(4.some >>= composed)
  println(4.some >>= g <=< f)
  println(4.some >>= f >=> g)
  println(4.some >>= g >=> f)

  /**
    * As a bonus, Scalaz defines Reader as a special case of Kleisli as follows:
    *
    * type ReaderT[F[+_], E, A] = Kleisli[F, E, A]
    *
    * type Reader[E, A] = ReaderT[Id, E, A]
    *
    * object Reader {
    *   def apply[E, A](f: E => A): Reader[E, A] = Kleisli[Id, E, A](f)
    * }
    * */
  val addStuff: Reader[Int, Int] =
    for {
      a <- Reader { (_: Int) * 2 }
      b <- Reader { (_: Int) + 10 }
    } yield a + b

  println(addStuff(3))
  println(addStuff(4))
  println(addStuff(5))
}
