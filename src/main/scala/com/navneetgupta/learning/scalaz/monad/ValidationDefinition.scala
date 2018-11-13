package com.navneetgupta.learning.scalaz.monad

object ValidationDefinition {

  sealed trait Validation[+E, +A] {
    /** Return `true` if this validation is success. */
    def isSuccess: Boolean = this match {
      case Success(_) => true
      case Failure(_) => false
    }

    /** Return `true` if this validation is failure. */
    def isFailure: Boolean = !isSuccess
  }

  private case class Success[E, A](a: A) extends Validation[E, A]
  private case class Failure[E, A](e: E) extends Validation[E, A]

  // ValidationOps introduces success[X], successNel[X], failure[X], and failureNel[X] methods to all data types

  sealed trait NonEmptyList[+A] {
    val head: A
    val tail: List[A]
    //def <::[AA >: A](b: AA): NonEmptyList[AA] = nel(b, head :: tail)
  }


}
