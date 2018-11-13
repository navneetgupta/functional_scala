package com.navneetgupta.learning.scalaz.monad

object EitherMethods {


  sealed trait \/[+A, +B] {
    def isLeft: Boolean = this match {
      case -\/(_) => true
      case \/-(_) => false
    }

    /** Return `true` if this disjunction is right. */
    def isRight: Boolean = this match {
      case -\/(_) => false
      case \/-(_) => true
    }

    /** Flip the left/right values in this disjunction. Alias for `unary_~` */
    def swap: (B \/ A) = this match {
      case -\/(a) => \/-(a)
      case \/-(b) => -\/(b)
    }

    /** Flip the left/right values in this disjunction. Alias for `swap` */
    def unary_~ : (B \/ A) = swap

    /** Return the right value of this disjunction or the given default if left. Alias for `|` */
    def getOrElse[BB >: B](x: => BB): BB =
      this match {
        case -\/(_) => x
        case \/-(b) => b
      }

    /** Return the right value of this disjunction or the given default if left. Alias for `ge */
    def |[BB >: B](x: => BB): BB = getOrElse(x)

    /** Return this if it is a right, otherwise, return the given value. Alias for `||` */
    def orElse[AA >: A, BB >: B](x: => AA \/ BB): AA \/ BB = this match {
      case -\/(_) => x
      case \/-(_) => this
    }

    /** Return this if it is a right, otherwise, return the given value. Alias for `oEe` */
    def |||[AA >: A, BB >: B](x: => AA \/ BB): AA \/ BB = orElse(x)
  }

  private case class -\/[+A](a: A) extends (A \/ Nothing)

  private case class \/-[+B](b: B) extends (Nothing \/ B)


}
