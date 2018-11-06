package com.navneetgupta.learning.scalaz

import scalaz.Scalaz._
import scalaz._

object FucntorEx extends App {
  val v1: CMayBe[Int] = CJust(10)
  val liftedCMayBe: (CMayBe[Int] => CMayBe[Int]) = Functor[CMayBe].lift {
    (_: Int) * 2
  } // a function from one Functor to another F[A] => F[B]
  val a = ((x: Int) => x + 1) map (_ * 7)

  implicit val cMayBeFunctor: Functor[CMayBe] = new Functor[CMayBe] {
    def map[A, B](fa: CMayBe[A])(f: A => B): CMayBe[B] = fa match {
      case CNothing => CNothing
      case CJust(x) => CJust(f(x))
    }
  }

  //  Functor typeclass, which is basically for things that can be mapped over.
  sealed trait CMayBe[+A]

  println(v1 map {
    _ * 10
  })
  println(v1)

  sealed trait Tree[+A]

  println(liftedCMayBe(v1))

  println(List(1, 2, 3) map (_ * 3)) // Normal List map function
  println(Functor[List].map(List(1, 2, 3))(_ * 3)) // Functor'' map on list


  //Function as Functors

  case class CJust[A](a: A) extends CMayBe[A]

  println(a(6))

  //  This is interesting. Basically map gives us a way to compose functions, except the order is in reverse from f compose g.
  // No wonder Scalaz provides as an alias of map. Another way of looking at Function1 is that it’s an infinite map from the domain to the range.

  case class Node[A](val a: A, val left: Tree[A], val right: Tree[A]) extends Tree[A]

  object CNothing extends CMayBe[Nothing]

  case object EmptyTree extends Tree[Nothing]

  implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
    def map[A, B](tree: Tree[A])(f: A => B): Tree[B] = tree match {
      case EmptyTree => EmptyTree
      case Node(a, left, right) => Node(f(a), map(left)(f), map(right)(f))
    }
  }


  // Laws
  // 1 . identity function as f should return same Functor ie map(tree)(id) = tree
  // 2.  fmap (f . g) F = fmap f (fmap g F)

  println(List(1, 2, 3) >| "x")
  println(List(1, 2, 3) as "x")

  println(List(1, 2, 3).fpair)
  println(List(1, 2, 3).strengthL("x"))
  println(List(1, 2, 3).strengthR("x"))
  println(List(1, 2, 3).void)

}
