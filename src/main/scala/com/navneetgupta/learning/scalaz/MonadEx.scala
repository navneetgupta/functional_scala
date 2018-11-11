package com.navneetgupta.learning.scalaz

import scalaz._, Scalaz._

object MonadEx extends App {
  /**
    * Monads are a natural extension applicative functors, and they pro- vide a solution to the following problem:
    * If we have a value with context, m a, how do we apply it to a function that takes a normal a and returns a value with a context
    *
    * trait Monad[F[_]] extends Applicative[F] with Bind[F] { self => ... }
    *
    * It extends Applicative and Bind. So let’s look at Bind.
    *
    * trait Bind[F[_]] extends Apply[F] { self =>
    * /** Equivalent to `join(map(fa)(f))`. */
    *
    * def bind[A, B](fa: F[A])(f: A => F[B]): F[B]  // flatMap
    * }
    *
    *
    * trait BindOps[F[_],A] extends Ops[F[A]] {
    * implicit def F: Bind[F]
    * ////
    *
    * import Liskov.<~<
    *
    * def flatMap[B](f: A => F[B]) = F.bind(self)(f)
    * def >>=[B](f: A => F[B]) = F.bind(self)(f)
    * def ∗[B](f: A => F[B]) = F.bind(self)(f)
    * def join[B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
    * def [B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
    * def >>[B](b: F[B]): F[B] = F.bind(self)(_ => b)
    * def ifM[B](ifTrue: => F[B], ifFalse: => F[B])(implicit ev: A <~< Boolean): F[B] = {
    * val value: F[Boolean] = Liskov.co[F, A, Boolean](ev)(self)
    *         F.ifM(value, ifTrue, ifFalse)
    * }
    * }
    **/

  // Since Monad Extends Applicative , it also has point method.

  assert((Monad[Option].point(3)) == Some(3))

  type Birds = Int

  //  case class Pole(left: Birds, right: Birds)
  case class Pole(left: Birds, right: Birds) {
    def landLeft(n: Birds): Pole = copy(left = left + n)

    def landRight(n: Birds): Pole = copy(right = right + n)
  }

  Pole(0, 0).landLeft(10)
  Pole(0, 0).landRight(12)

  case class Pole2(left: Birds, right: Birds) {
    def landLeft(n: Birds): Option[Pole2] =
      if (math.abs(left + n - right) < 4) copy(left = left + n).some
      else none[Pole2]

    def landRight(n: Birds): Option[Pole2] =
      if (math.abs(right + n - left) < 4) copy(right = right + n).some
      else none[Pole2]

    def banana: Option[Pole2] = none
  }

  assert(Pole2(0, 0).landLeft(2).flatMap(_.landRight(2)) == Some(Pole2(2, 2)))

  assert((none: Option[Pole2]).flatMap(_.landLeft(2)).flatMap(_.landRight(4)) == none[Pole2])
  assert(Monad[Option].point(Pole2(2, 3)).flatMap(_.landRight(2)) == Some(Pole2(2, 5)))

  assert((Monad[Option].point(Pole2(0, 0)) >>= {
    _.landRight(2)
  } >>= {
    _.landLeft(2)
  } >>= {
    _.landRight(3)
  }) == Pole2(2, 5).some)

  // Instead of the do notation in Haskell, Scala has for syntax, which does the same thing:
  assert(
    (for {
      p <- Monad[Option].point(Pole2(0, 0))
      q <- p.landRight(2)
      r <- q.landLeft(2)
      s <- r.landRight(3)
    } yield s) == Pole2(2, 5).some
  )

  // Instead of making functions that ignore their input and just return a predetermined monadic value, we can use the >> function.

  assert((3.some >> 4.some) == 4.some)
  assert((none[Pole2] >> 3.some) == none[Pole2])
  assert((3.some >> none[Pole2]) == none[Pole2])


  assert((
    for {
      x <- 1 |-> 50 if x.shows contains '7'
    } yield x
    ) == List(7, 17, 27, 37, 47))

  // The MonadPlus type class is for monads that can also act as monoids.
  /**
    * trait MonadPlus[F[_]] extends Monad[F] with ApplicativePlus[F] { self => }
    **/


  // Plus, PlusEmpty, and ApplicativePlus

  /**
    * trait ApplicativePlus[F[_]] extends Applicative[F] with PlusEmpty[F] { self => ...
    * }
    *
    * trait PlusEmpty[F[_]] extends Plus[F] { self => ////
    * def empty[A]: F[A]
    * }
    *
    * trait Plus[F[_]] { self =>
    * def plus[A](a: F[A], b: => F[A]): F[A]
    * }
    *
    * Similar to Semigroup[A] and Monoid[A], Plus[F[_]] and PlusEmpty[F[_]] requires their instances to implement plus and empty,
    * but at the type constructor ( F[_]) level.
    *
    * Plus introduces <+> operator to append two containers:
    **/


  assert((List(1, 2, 3) <+> List(4, 5, 6)) == List(1, 2, 3, 4, 5, 6))

  assert(((Some(5): Option[Int]) <+> none[Int]) == 5.some)

  assert((5.some <+> 6.some) == 5.some)


  case class KnightPos(c: Int, r: Int) {
    def move: List[KnightPos] =
      for {
        KnightPos(c2, r2) <- List(KnightPos(c + 2, r - 1), KnightPos(c + 2, r + 1),
          KnightPos(c - 2, r - 1), KnightPos(c - 2, r + 1), KnightPos(c + 1, r - 2),
          KnightPos(c + 1, r + 2), KnightPos(c - 1, r - 2), KnightPos(c - 1, r + 2))

        if (((1 |-> 8) contains c2) && ((1 |-> 8) contains r2))
      } yield KnightPos(c2, r2)

    def in3: List[KnightPos] =
      for {
        first <- move
        second <- first.move
        third <- second.move
      } yield third

    def canReachIn3(end: KnightPos): Boolean = in3 contains end
  }

  assert((KnightPos(6, 2) canReachIn3 KnightPos(6, 1)) == true)
  assert((KnightPos(6, 2) canReachIn3 KnightPos(7, 3)) == false)

  /**
    * Monad law:
    *
    * Left Identity: (Monad[F].point(x) flatMap {f}) assert_=== f(x)
    * Right Identity: (m forMap {Monad[F].point(_)}) assert_=== m
    * Associativity: (m flatMap f) flatMap g assert_=== m flatMap { x => f(x) flatMap {g} }
    * */
}
