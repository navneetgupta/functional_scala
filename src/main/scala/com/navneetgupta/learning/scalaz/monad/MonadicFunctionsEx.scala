package com.navneetgupta.learning.scalaz.monad

import scalaz._, Scalaz._

object MonadicFunctionsEx extends App {
  /**
    * explore a few functions that either op- erate on monadic values or return monadic values as their results (or both!).
    * Such functions are usually referred to as monadic functions.
    **/

  /*In Scalaz join (and its symbolic alias ) is a method introduced by Bind:
  trait BindOps[F[_],A] extends Ops[F[A]] {
    def join[B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
    def [B](implicit ev: A <~< F[B]): F[B] = F.bind(self)(ev(_))
  }*/

  println((Some(9.some): Option[Option[Int]]).join)  // Some(9)
  println((Some(none): Option[Option[Int]]).join) // None
  println(List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9)).join) // List(1, 2, 3, 4, 5, 6, 7, 8, 9)

  println(9.right[String].right[String].join) // \/-(9)
  println("boom".left[Int].right[String].join) // -\/(boom)

  println(List(1, 2, 3) filterM { x => List(true, false) }) // List(List(1, 2, 3), List(1, 2), List(1, 3), List(1), List(2, 3), List(2), List(3), List())
  println(Vector(1, 2, 3) filterM { x => Vector(true, false) }) //Vector(Vector(1, 2, 3), Vector(1, 2), Vector(1, 3), Vector(1), Vector(2, 3), Vector(2), Vector(3), Vector())



  // RPN Calculator
  def foldingFunction(list: List[Double], next: String): List[Double] = {
    (list, next) match {
      case (x :: y :: ys, "*") => (y * x) :: ys
      case (x :: y :: ys, "+") => (y + x) :: ys
      case (x :: y :: ys, "-") => (y - x) :: ys
      case (xs, numString) => numString.toInt :: xs
    }
  }

  def solveRPN(s: String): Double =
    (s.split(' ').toList.foldLeft(Nil: List[Double]) {foldingFunction}).head

  // The next step is to change the folding function to handle errors gracefully. Scalaz adds parseInt to String which returns Validation[NumberFormatException, Int]. We can call toOption on a validation to turn it into Option[Int] like the book

  println("1".parseInt.toOption)
  println("waw".parseInt.toOption)

  def foldingFunction2(list: List[Double], next: String): Option[List[Double]] =
    (list,next) match {
      case (x :: y :: ys, "*") => ((y * x) :: ys).point[Option]
      case (x :: y :: ys, "+") => ((y + x) :: ys).point[Option]
      case (x :: y :: ys, "-") => ((y - x) :: ys).point[Option]
      case (xs, numString) => numString.parseInt.toOption map {_ :: xs}
    }

  def solveRPN2(s: String): Option[Double] = for {
    List(x) <- s.split(' ').toList.foldLeftM(Nil: List[Double]) {foldingFunction2}
  } yield x


  println("===============================================")
  println(solveRPN("1 2 * 4 +"))
  println(solveRPN("1 2 * 4"))
 // println(solveRPN("1 8 garbage"))
  println(solveRPN("10 4 3 + 2 * -"))
  println(solveRPN2("1 2 * 4 +"))
  println(solveRPN2("1 2 * 4"))
  println(solveRPN2("1 8 garbage"))
  println(solveRPN2("10 4 3 + 2 * -"))


}
