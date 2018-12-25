package com.navneetgupta.scalaz.stackless

import scalaz.Free.Trampoline
import scalaz.Trampoline._

object OddEvenEx extends App {

  // Calculate if a list have odd or even number of elements

  def odd[A](xs: List[A]): Boolean = {
    xs match {
      case Nil => false
      case _ :: tail => even(tail)
    }
  }

  def even[A](xs: List[A]): Boolean = {
    xs match {
      case Nil => true
      case _ :: tail => odd(tail)
    }
  }


  println(odd(List(1, 2, 3)))

  // println(odd((1 to 1000000).toList)) // This will stack overflow

  // To avoid Stack Overflow Without TCO(Tail recursion optimization we uses trampoline)

  def odd2[A](xs: List[A]) : Trampoline[Boolean] = {
    xs match {
      case Nil => done(false)
      case _ :: tail => suspend(even2(tail))
    }
  }

  def even2[A](xs: List[A]) : Trampoline[Boolean] = {
    xs match {
      case Nil => done(false)
      case _ :: tail => suspend(odd2(tail))
    }
  }

  println(odd2(List(1, 2, 3)))
  println(odd2((1 to 1000000).toList))
}
