package com.navneetgupta.learning.scalaz

import scalaz._, Scalaz._

object CanTruthy2Ex extends App {

  trait CanTruthy2[A] {
    def truthys(a: A): Boolean
  }

  object CanTruthy2 {
    def apply[A: CanTruthy2](implicit ev: CanTruthy2[A]): CanTruthy2[A] = ev

    def truthys[A](f: A => Boolean): CanTruthy2[A] = new CanTruthy2[A] {
      override def truthys(a: A): Boolean = f(a)
    }
  }

  trait CanTruthy2Ops[A] {
    def self: A
    implicit def F: CanTruthy2[A]
    final def truthy: Boolean = F.truthys(self)
  }
  object ToCanIsTruthy2Ops {
    implicit def toCanIsTruthy2Ops[A](v: A)(implicit ev: CanTruthy2[A]) =
      new CanTruthy2Ops[A] {
        override def self = v
        override implicit def F: CanTruthy2[A] = ev
      }
  }

  import CanTruthy2._
  import ToCanIsTruthy2Ops._

  implicit val intCanTruthy: CanTruthy2[Int] = CanTruthy2.truthys({
    case 0 => false
    case _ => true
  })

  implicit def listCanTruthy[A]: CanTruthy2[List[A]] = CanTruthy2.truthys({
    case Nil => false
    case _   => true
  })

  implicit val booleanCanTruthy: CanTruthy2[Boolean] = CanTruthy2.truthys(identity)

  implicit val nilCanTruthy: CanTruthy2[scala.collection.immutable.Nil.type] = CanTruthy2.truthys(_ => false)

  implicit def optionCanTruthy[A]: CanTruthy2[Option[A]] = CanTruthy2.truthys({
    case None    => false
    case Some(0) => false // depending on what is required it can be changed, Here Assuming that even a zero value wrapped in Option should return false
    case _       => true
  })

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
