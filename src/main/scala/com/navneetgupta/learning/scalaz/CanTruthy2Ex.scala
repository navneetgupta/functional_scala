package com.navneetgupta.learning.scalaz

import scalaz.Scalaz._

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

  implicit def listCanTruthy[A]: CanTruthy2[List[A]] = CanTruthy2.truthys({
    case Nil => false
    case _ => true
  })

  import ToCanIsTruthy2Ops._

  implicit val intCanTruthy: CanTruthy2[Int] = CanTruthy2.truthys({
    case 0 => false
    case _ => true
  })

  implicit def optionCanTruthy[A]: CanTruthy2[Option[A]] = CanTruthy2.truthys({
    case None => false
    case Some(0) => false // depending on what is required it can be changed, Here Assuming that even a zero value wrapped in Option should return false
    case _ => true
  })

  implicit val booleanCanTruthy: CanTruthy2[Boolean] = CanTruthy2.truthys(identity)

  implicit val nilCanTruthy: CanTruthy2[scala.collection.immutable.Nil.type] = CanTruthy2.truthys(_ => false)

  /**
    * warning: implicit conversion method toCanIsTruthyOps should be enabled
    * by making the implicit value scala.language.implicitConversions visible.
    * This can be achieved by adding the import clause 'import scala.language.implicitConversions'
    * or by setting the compiler option -language:implicitConversions.
    * See the Scaladoc for value scala.language.implicitConversions for a discussion
    * why the feature should be explicitly enabled.
    *
    * implicit def toCanIsTruthyOps[A](v: A)(implicit ev: CanTruthy[A]) = new CanTruthyOps[A] {
    */
  object ToCanIsTruthy2Ops {
    implicit def toCanIsTruthy2Ops[A](v: A)(implicit ev: CanTruthy2[A]) =
      new CanTruthy2Ops[A] {
        override def self = v

        override implicit def F: CanTruthy2[A] = ev
      }
  }

  println(1.some.truthy)
  println(1.truthy)
  println(0.truthy)
  println(0.some.truthy)
  println(10.some.truthy)
  println((-11).some.truthy)
  println(List(1, 2, 3, 4).truthy)
  println(Nil.truthy)
  println(false.truthy)
}

object App23 {

  trait CanTruthy[A] {
    self =>
    def truthys(a: A): Boolean
  }

  trait CanTruthyOps[A] {
    def self: A

    implicit def F: CanTruthy[A]

    final def truthy: Boolean = F.truthys(self)
  }

  object CanTruthy {
    def apply[A](implicit ev: CanTruthy[A]): CanTruthy[A] = ev

    def truthys[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
      def truthys(a: A): Boolean = f(a)
    }
  }

  object ToCanIsTruthyOps {
    implicit def toCanIsTruthyOps[A](v: A)(implicit ev: CanTruthy[A]) = new CanTruthyOps[A] {
      def self = v

      implicit def F: CanTruthy[A] = ev
    }
  }

}
