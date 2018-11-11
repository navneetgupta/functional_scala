package com.navneetgupta.learning.scalaz

//import org.scalatest.Assertions._
import scalaz.Scalaz._
import scalaz._

object MonoidEx extends App {
  /**
    * trait Monoid[A] extends Semigroup[A] { self => ////
    * /** The identity element for `append`. */
    * def zero: A ...
    * }
    *
    * trait Semigroup[A] { self =>
    * def append(a1: A, a2: => A): A ...
    * }
    *
    * trait SemigroupOps[A] extends Ops[A] {
    * final def |+|(other: => A): A = A.append(self, other)
    * final def mappend(other: => A): A = A.append(self, other)
    * final def (other: => A): A = A.append(self, other)
    * }
    *
    **/

  assert((List(1, 2, 3) |+| List(4, 5, 6)) == List(1, 2, 3, 4, 5, 6))
  assert((List(1, 2, 3) mappend List(4, 5, 6)) == List(1, 2, 3, 4, 5, 6))
  assert(("one" mappend "two") == "onetwo")
  assert(("one" |+| "two") == "onetwo")

  /**
    * So now that there are two equally valid ways for numbers (addition and multiplication) to be monoids,
    * which way do choose? Well, we don’t have to.
    * This is where Scalaz 7.1 uses tagged type.
    * The built-in tags are Tags.
    * There are 8 tags for Monoids and 1 named Zip for Applicative.
    * (Is this the Zip List I couldn’t find yesterday?)
    **/

  assert((Tags.Multiplication(10) |+| Monoid[Int @@ Tags.Multiplication].zero) == Tags.Multiplication(10)) // 10 * 1 zero is 1 for multiplication
  assert((10 |+| Monoid[Int].zero) == 10) // zero for defult Monoid[Int] instance is 0 and append as addition

  /**
    * Another type which can act like a monoid in two distinct but equally valid ways is Bool.
    * The first way is to have the or function || act as the binary function along with False as the identity value.
    * The other way for Bool to be an instance of Monoid is to kind of do the opposite: have && be the binary function and then make True the identity value.
    *
    * In Scalaz 7 these are called Boolean @@ Tags.Disjunction and Boolean @@ Tags.Conjunction respectively.
    **/

  assert((Tags.Disjunction(true) |+| Tags.Disjunction(false)) == Tags.Disjunction(true)) // true || false => true
  assert((Tags.Conjunction(true) |+| Tags.Conjunction(false)) == Tags.Conjunction(false)) // true && false => false
  assert((Monoid[Boolean @@ Tags.Conjunction].zero |+| Tags.Conjunction(true)) == Tags.Conjunction(true)) // Monoid[Boolean @@ Tags.Conjunction].zero is true
  assert((Monoid[Boolean @@ Tags.Disjunction].zero |+| Tags.Disjunction(true)) == Tags.Disjunction(true))
  assert((Monoid[Boolean @@ Tags.Disjunction].zero |+| Tags.Disjunction(false)) == Tags.Disjunction(false))

  assert(((Ordering.LT: Ordering) |+| (Ordering.GT: Ordering)) == Ordering.LT)
  assert(((Ordering.LT: Ordering) |+| (Ordering.GT: Ordering)) == Ordering.LT)


  /**
    * OK, so how is this monoid useful?
    *
    * Let’s say you were writing a function that takes two strings, compares their lengths, and returns an Ordering.
    * But if the strings are of the same length, then instead of returning EQ right away, we want to compare them alphabetically.
    *
    * Because the left comparison is kept unless it’s Ordering.EQ we can use this to compose two levels of comparisons.
    * Let’s try implementing lengthCompare using Scalaz:
    **/

  def lengthCompare(lhs: String, rhs: String): Ordering = (lhs.length ?|? rhs.length) |+| (lhs ?|? rhs)

  assert((lengthCompare("zen", "ants")) == Ordering.LT)
  assert((lengthCompare("zen", "ant")) == Ordering.GT)

  /**
    *
    * Functor Laws:
    *   1.  mapping id function over a functor F gives back the same Functor F
    *
    * assert((List(1, 2, 3) map {identity} )== List(1, 2, 3))
    *   2.  compose two function `f` and `g` and then map over a functor is same as first mapping `f` over Functor then again mapping second function
    * `g` over the functor from result of first mapping.
    *
    *
    * assert(
    * (List(1, 2, 3)
    * .map { // f compose g
    * {(_: Int) * 3}
    * map {(_: Int) + 1}
    * }) == (List(1, 2, 3) map {(_: Int) * 3} map {(_: Int) + 1}))
    *
    **/

  /*trait FunctorLaw1 {
    /** The identity function, lifted, is a no-op. */
    def identity[A](fa: F[A])(implicit FA: Equal[F[A]]): Boolean = FA.equal(map(fa)(x => x), fa)

    /**
      * A series of maps may be freely rewritten as a single map on a
      * composed function.
      */
    def associative[A, B, C](fa: F[A], f1: A => B, f2: B => C)(implicit FC: Equal[F[C]]): Boolean = FC.equal(map(map(fa)(f1))(f2), map(fa)(f2 compose f1))
  }*/

  // Option as Monoid.

  implicit def optionMonoid1[A: Semigroup]: Monoid[Option[A]] = new Monoid[Option[A]] {
    override def append(f1: Option[A], f2: => Option[A]): Option[A] = {
      (f1, f2) match {
        case (Some(a1), Some(a2)) => Semigroup[A].append(a1, a2).some
        case (Some(a1), None) => f1
        case (None, Some(a2)) => f2
        case _ => None
      }
    }

    override def zero: Option[A] = None
  }

  // Above works only if A works as Monoid to use mappend on the A,
  // But if we don’t know if the contents are monoids, we can’t use mappend between them, so what are we to do?
  // Well, one thing we can do is to just discard the second value and keep the first one. For this, the First a type exists.
  // Haskell is using newtype to implement First type constructor. Scalaz 7 does it using mightly Tagged type:


  assert((Tags.First('a'.some) |+| Tags.First('b'.some)) == Tags.First('a'.some))
  assert((Tags.First('a'.some) |+| Tags.First(none: Option[Char])) == Tags.First('a'.some))
  assert((Tags.First(none: Option[Char]) |+| Tags.First('b'.some)) == Tags.First('b'.some))

  // If we want to keep second parameter we can use Last a type.

  assert((Tags.Last('a'.some) |+| Tags.Last('b'.some)) == Tags.Last('b'.some))
  assert((Tags.Last('a'.some) |+| Tags.Last(none: Option[Char])) == Tags.Last('a'.some))
  assert((Tags.Last(none: Option[Char]) |+| Tags.Last('b'.some)) == Tags.Last('b'.some))
}
