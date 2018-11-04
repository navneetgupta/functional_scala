package com.navneetgupta.cats.functors

import cats.Functor
import cats.implicits._
import com.navneetgupta.common._

/**
  * functors, an abstraction that allows us to represent sequences of operations within a context such as a List, an Option,
  * or any one of a thousand other possibilities.
  * Functors on their own aren’t so useful, but special cases of functors such as monads and applica􏰁ve functors are some of the most
  * commonly used abstraction
  *
  * Informally, a functor is anything with a map method. Ex: Option, List, and Either, Future etc.
  *
  * Because map leaves the structure of the context unchanged, we can call it repeatedly to sequence multiple computations
  * on the contents of an initial data structure:
  *
  * It turns out that single argument func􏰁ons are also functors.
  *
  * Formally, a functor is a type F[A] with an opera􏰁on map with type (A => B) => F[B]
  *
  * trait Functor[F[_]] {
  * def map[A, B](fa: F[A])(f: A => B): F[B]
  * }
  *
  * Functor Laws:
  *
  * Identity: calling map with the identity function is the same as doing nothing: fa.map(a => a) == fa
  * Composition: mapping with two functions f and g is the same as mapping with f and then mapping with g: fa.map(g(f(_))) == fa.map(f).map(g)
  */

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]

object Tree {
  def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
    Branch(left, right)

  def leaf[A](value: A): Tree[A] =
    Leaf(value)
}

object CustomInstances {
  implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
    override def map[A, B](a: Tree[A])(f: A => B): Tree[B] = a match {
      case Leaf(value) => Leaf(f(value))
      case Branch(left, right) => Branch(map(left)(f), map(right)(f))
    }
  }
}

object Data {

  import Tree._

  val t1: Tree[Int] = branch(branch(leaf(10), leaf(9)), branch(branch(branch(leaf(10), leaf(1)), leaf(3)), branch(leaf(4), branch(leaf(5), leaf(6)))))
}

object FunctorsEx extends App {

  import CustomInstances._

  val treeFunctorRes = Data.t1.map(_ * 2)
  println(treeFunctorRes)
}

object FunctorsEx2 extends App {

  import CustomInstances._

  val treeFunctorRes = implicitly[Functor[Tree]].map(Data.t1)(_ * 2)
  println(treeFunctorRes)
}

object FunctorsEx3 extends App {

  import CustomInstances._

  val treeFunctorRes = Functor.apply[Tree].map(Data.t1)(_ * 2)
  println(treeFunctorRes)

}
