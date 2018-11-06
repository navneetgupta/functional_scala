package com.navneetgupta.learning.scalaz

import scalaz.Scalaz._

object CanTruthyApp extends App {

  trait CanTruthy[A] {
    def truthys(a: A): Boolean
  }

  object CanTruthy {

    def apply[A: CanTruthy]: CanTruthy[A] = implicitly[CanTruthy[A]]

    implicit class CanTruthyOps[A: CanTruthy](a: A) {
      def truthys: Boolean = CanTruthy[A].truthys(a)
    }

  }

  import CanTruthy._

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

  println(1.some.truthys)
  println(1.truthys)
  println(0.truthys)
  println(0.some.truthys)
  println(10.some.truthys)
  println((-11).some.truthys)
  println(List(1, 2, 3, 4).truthys)
  println((Nil: List[String]).truthys)
  println(false.truthys)
}
