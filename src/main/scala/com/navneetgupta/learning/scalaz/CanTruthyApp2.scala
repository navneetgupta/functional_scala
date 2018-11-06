package com.navneetgupta.learning.scalaz

//object APPP {
import scalaz.Scalaz._
import simulacrum._

object CanTruthyApp2 extends App {

  @typeclass trait CanTruthy[A] {
    @op("truthy") def truthys(a: A): Boolean
  }

  /**
    * Underneath for above @typeclass and @op definition `simulacrum` will define below boilerplate lines of code as following
    *
    * object CanTruthy {
    * def apply[A](implicit instance: CanTruthy[A]): CanTruthy[A] = instance
    *
    * def truthys[A](f: A => Boolean): CanTruthy2[A] = new CanTruthy2[A] {
    * override def truthys(a: A): Boolean = f(a)
    * }
    *
    * trait Ops[A] {
    * def typeClassInstance: CanTruthy[A]
    * def self: A
    * final def truthy: Boolean = typeClassInstance.truthys(self)
    * }
    *
    * trait ToCanTruthyOps {
    * implicit def toCanTruthyOps[A](v: A)(implicit ev: CanTruthy[A]): Ops[A] = new Ops[A] {
    * val self = v
    * val typeClassInstance = tc
    * }
    * }
    *
    * object nonInheritedOps extends ToCanTruthyOps
    *
    * trait AllOps[A] extends Ops[A] {
    * def typeClassInstance: CanTruthy[A]
    * }
    *
    *
    * object ops {
    * implicit def toAllCanTruthyOps[A](target: A)(implicit tc: CanTruthy[A]): AllOps[A] = new AllOps[A] {
    * val self = target
    * val typeClassInstance = tc
    * }
    * }
    * }
    *
    */

  import CanTruthy.ops._

  implicit val intCanTruthy: CanTruthy[Int] = new CanTruthy[Int] {
    def truthys(a: Int): Boolean = a match {
      case 0 => false
      case _ => true
    }
  }

  implicit def listCanTruthy[A]: CanTruthy[List[A]] = new CanTruthy[List[A]] {
    def truthys(a: List[A]): Boolean = a match {
      case Nil => false
      case _ => true
    }
  }

  implicit val booleanCanTruthy: CanTruthy[Boolean] = new CanTruthy[Boolean] {
    def truthys(a: Boolean): Boolean = identity(a)
  }

  implicit def optionCanTruthy[A]: CanTruthy[Option[A]] = new CanTruthy[Option[A]] {
    def truthys(a: Option[A]): Boolean = a match {
      case None => false
      case Some(0) => false // depending on what is required it can be changed, Here Assuming that even a zero value wrapped in Option should return false
      case _ => true
    }
  }

  implicit val nilCanTruthy: CanTruthy[scala.collection.immutable.Nil.type] = new CanTruthy[scala.collection.immutable.Nil.type] {
    def truthys(a: scala.collection.immutable.Nil.type): Boolean = false
  }

  println(1.some.truthy)
  println(1.truthy)
  println(0.truthy)
  println(0.some.truthy)
  println(10.some.truthy)
  println(-11.some.truthy)
  println(List(1, 2, 3, 4).truthy)
  println(Nil.truthy)
  println(false.truthy)
}
