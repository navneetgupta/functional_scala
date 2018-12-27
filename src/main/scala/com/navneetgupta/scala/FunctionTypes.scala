package com.navneetgupta.scala

object FunctionTypes extends App {
  val add10 = (a:Int)  => a+10
  val toUpper = (a: String) => a.toUpperCase

  // :t -v add10 =>  Int => Int => abstract trait Function1[-T1, +R] extends AnyRef

  // First Class Functions In Scala are represented as FunctionX classes X => number of Args
  // Function1[-T1, +R] =>

  /**
    * 'Function[-A, +B]' is:
    *
    * Contravariant in the input type 'A' (marked with '-') where 'A' can be replaced with the derived type. (more general input argument)
    * Covariant in the output type 'B' (marked with '+' ) where 'B' can be replaced with the base type. (more specific return type)
    * Following this rule:
    *
    * “Be conservative in what you do, be liberal in what you accept from others” — robustness principle
    *
    * */

 /* abstract class List[+T] { self =>
    // lets define a contains method
    def contains(a: T): Boolean = self match{
      case Cons(x, xs) if x == a => true
      case Cons(x, xs) => xs.contains(a)
      case Nil => false
    }
  }
  case object Nil extends List[Nothing]
  case class Cons[+T](head: T, tail: List[T]) extends List[T]*/

  // The above contains defination Shows below Compile time error
  // Covariant type T occurs in contravariant position in type T of value a

  // 'Function[-A, +B]' and mentioned that the inputs of functions are contravariant and their output are covariant.
  // In our case, 'List' is covariant in 'T',
  // The problem is in the input argument of 'contains' the element is a value of type 'T' which is covariant,
  // we need to make it contravariant to be able to pass it into 'Function1'
  // => make it Contravariant by applyign a Lower Bound

  abstract class List[+T] { self =>
    // lets define a contains method
    def contains[T1 >: T](a: T1): Boolean = self match{
      case Cons(x, xs) if x == a => true
      case Cons(x, xs) => xs.contains(a)
      case Nil => false
    }
  }
  case object Nil extends List[Nothing]
  case class Cons[+T](head: T, tail: List[T]) extends List[T]

  // If you have a contravariant type parameter and you need to define a function that returns a value of that type,
  // you’ll need to use the term '<:' to make the output covariant.
}
