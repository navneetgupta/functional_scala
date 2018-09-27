package com.navneetgupta.kittens

import cats.Functor
import cats.derived
import com.navneetgupta.common._
import cats.implicits._

sealed trait Tree[+A]
final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
final case class Leaf[A](value: A) extends Tree[A]

object Tree {
  def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
    Branch(left, right)
  def leaf[A](value: A): Tree[A] =
    Leaf(value)
}

object FucntorData {
  import Tree._
  val t1: Tree[Int] = branch(branch(leaf(10), leaf(9)), branch(branch(branch(leaf(10), leaf(1)), leaf(3)), branch(leaf(4), branch(leaf(5), leaf(6)))))
}

object FunctorEx extends App {
  implicit val fc: Functor[Tree] = {
    import derived.auto.functor._
    derived.semi.functor
  }

  val treeFunctorRes = FucntorData.t1.map(_ * 2)
  println(treeFunctorRes)
}
