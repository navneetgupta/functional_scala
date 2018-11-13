package com.navneetgupta.learning.scalaz.monad


import scalaz._, Scalaz._

object EitherEx extends App {

  /**
    * The Either e a type on the other hand, allows us to incorporate a context of possible failure to our values while also being able to
    * attach values to the failure, so that they can describe what went wrong or provide some other useful info regarding the failure.
    *
    * We know Either[A, B] from the standard library, but Scalaz 7 implements its own Either equivalent named \/:
    *
    * sealed trait \/[+A, +B] {
    * def isLeft: Boolean = this match {
    * case -\/(_) => true
    * case \/-(_) => false
    * }
    *
    * /** Return `true` if this disjunction is right. */
    * def isRight: Boolean = this match {
    * case -\/(_) => false
    * case \/-(_) => true
    * }
    *
    * ...
    * /** Flip the left/right values in this disjunction. Alias for `unary_~` */
    *     def swap: (B \/ A) = this match {
    *         case -\/(a) => \/-(a)
    *         case \/-(b) => -\/(b)
    *     }
    *
    *     /** Flip the left/right values in this disjunction. Alias for `swap` */
    *     def unary_~ : (B \/ A) = swap
    *
    *     ...
    *     /** Return the right value of this disjunction or the given default if left. Alias for `|` */
    *     def getOrElse[BB >: B](x: => BB): BB =
    *         toOption getOrElse x
    *
    *     /** Return the right value of this disjunction or the given default if left. Alias for `ge */
    *     def |[BB >: B](x: => BB): BB = getOrElse(x)
    *
    *     /** Return this if it is a right, otherwise, return the given value. Alias for `||` */
    *     def orElse[AA >: A, BB >: B](x: => AA \/ BB): AA \/ BB = this match {
    *         case -\/(_) => x
    *         case \/-(_) => this
    *     }
    *
    *       /** Return this if it is a right, otherwise, return the given value. Alias for `oEe` */
    *     def |||[AA >: A, BB >: B](x: => AA \/ BB): AA \/ BB = rx) * *  }
    *
    * private case class -\/[+A](a: A) extends (A \/ Nothing)
    * private case class \/-[+B](b: B) extends (Nothing \/ B)
    *
    **/

  println(1.right[String])
  println("error".left[Int])


  // The Either type in Scala standard library is not a monad on its own, which means it does not implement flatMap method with or without Scalaz:

  val a = for {
    e1 <- "event 1 ok".right
    e2 <- "event 2 failed!".left[String]
    e3 <- "event 3 failed!".left[String]
  } yield (e1 |+| e2 |+| e3)


  println(a)
  println(~"event 2 failed!".left[String] | "something good") // For left value, we can call swap method or it’s symbolic alias unary_~:
  println("event 1 ok".right map {_ + "!"}) // map to modify the right side value:

  println("event 1 failed!".left ||| "retry event 1 ok".right) //  The symbolic alias for orElse is |||:


}