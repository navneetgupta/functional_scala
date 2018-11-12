package com.navneetgupta.learning.scalaz.monad


import scalaz._, Scalaz._

object WritterMonadEx extends App {
  // Writer monad is for values that have another value wrapped that act as sort of log value.

  def isBigGang(x: Int): (Boolean, String) = (x > 9, "Compared gang size to 9")

  implicit class PairOps[A](pair: (A, String)) {
    def applyLog[B](f: A => (B, String)): (B, String) = {
      val (a, log) = pair
      val (b, newLog) = f(a)
      (b, log ++ newLog)
    }
  }

  assert((3, "Smallish Gang.").applyLog(isBigGang) == (false, "Smallish Gang.Compared gang size to 9"))

  implicit class PairOps2[A, B: Monoid](pair: (A, B)) {
    def applyLog2[C](f: A => (C, B)): (C, B) = {
      val (a, log) = pair
      val (c, newLog) = f(a)
      (c, log |+| newLog)
    }
  }

  assert((3, "Smallish Gang.").applyLog2(isBigGang) == (false, "Smallish Gang.Compared gang size to 9"))

  // Writer

  /**
    * To attach a monoid to a value, we just need to put them together in
    * a tuple. The Writer w a type is just a newtype wrapper for this.
    *
    * type Writer[+W, +A] = WriterT[Id, W, A]
    *
    * Writer[+W, +A] is a type alias for WriterT[Id, W, A].
    *
    *
    * sealed trait WriterT[F[+_], +W, +A] { self =>
    * val run: F[(W, A)]
    * def written(implicit F: Functor[F]): F[W] = F.map(run)(_._1)
    * def value(implicit F: Functor[F]): F[A] = F.map(run)(_._2)
    * }
    *
    * final class WriterOps[A](self: A) {
    * def set[W](w: W): Writer[W, A] = WriterT.writer(w -> self)
    * def tell: Writer[A, Unit] = WriterT.tell(self)
    * }
    **/

  3.set("Something") // Writer[String, Int]

  "Something".tell // Writer[String, Unit]


  def logNumber(x: Int): Writer[List[String], Int] = {
    x.set(List(s"Got Number: " + x.shows))
  }

  def multWithLog: Writer[List[String], Int] =
    for {
      a <- logNumber(3)
      b <- logNumber(5)
    } yield (a * b)

  println(multWithLog.run)

  def gcd(a: Int, b: Int): Writer[List[String], Int] =
    if (b == 0)
      for {
        _ <- List("Finished with " + a.shows).tell
      } yield a
    else
      List(a.shows + " mod " + b.shows + " = " + (a % b).shows).tell >>= {
        _ => gcd(b, a % b)
      }

  println(gcd(2, 3).run)

  println(gcd(8, 3).run)
  println(gcd(12, 6).run)
  println(gcd(36, 8).run)

  /**
    * When using the Writer monad, you have to be careful which monoid to use, because using lists can sometimes turn out
    * to be very slow. That’s because lists use ++ for mappend and using ++ to add some- thing to the end of a list is slow
    * if that list is really long.
    *
    * use Vector which has almost contstat effective time for any operation.
    */

  def gcd1(a: Int, b: Int): Writer[Vector[String], Int] =
    if (b == 0)
      for {
        _ <- Vector("Finished with " + a.shows).tell
      } yield a
    else
      Vector(a.shows + " mod " + b.shows + " = " + (a % b).shows).tell >>= {
        _ => gcd1(b, a % b)
      }

  println(gcd1(2, 3).run)

  println(gcd1(8, 3).run)
  println(gcd1(12, 6).run)
  println(gcd1(36, 8).run)


  val addStuff =
    for {
      a <- (_: Int) * 3
      b <- (_: Int) + 10
    } yield a + b

  println(addStuff(10))
  println(addStuff(3))
  println(addStuff(2))

  /**
    * Both (*2) and (+10) get applied to the number 3 in this case. return (a+b) does as well,
    * but it ignores it and always presents a+b as the result. For this reason, the function monad is also called the reader monad.
    * All the functions read from a common source.
    * Essentially, the reader monad lets us pretend the value is already there.
    * I am guessing that this works only for functions that accepts one parameter.
    * Unlike Option and List monads, neither Writer nor reader monad is available in the standard library.
    **/
}
