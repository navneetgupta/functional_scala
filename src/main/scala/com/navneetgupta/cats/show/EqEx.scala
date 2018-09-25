package com.navneetgupta.cats.show

import cats.Eq
import com.navneetgupta.common._

object CustomEqInstances {
  import cats.instances.int._
  import cats.instances.double._
  import cats.syntax.eq._
  /**
   * Eq is designed to support type-safe equality and address annoyances using Scala’s built-in == operator.
   *
   * == works for any pair of objects, no ma􏰀er what types we compare.
   *
   * Eq is designed to add some type safety to equality checks and work around this problem
   *
   * trait Eq[A] {
   *     def eqv(a: A, b: A): Boolean
   *     //other concrete methods based on eqv...
   * }
   *
   * The interface syntax, defined in cats.syntax.eq, provides two methods for performing equality
   * checks provided there is an instance Eq[A] in scope:
   *     • === compares two objects for equality;
   *     • =!= compares two objects for inequality.
   */

  implicit val studentEq: Eq[Student] = Eq.instance[Student] { (s1, s2) =>
    s1.id === s2.id
  }
  implicit val orderEq: Eq[Order] = Eq.instance[Order] { (o1, o2) =>
    o1.quantity === o2.quantity && o1.amount === o2.amount
  }

  // OptionEq can be used
  //  implicit def optionEq[A <: Eq[A]](implicit eqIns: Eq[A]): Eq[Option[A]] =
  //    Eq.instance[Option[A]] {
  //      (o1, o2) =>
  //        (o1, o2) match {
  //          case (Some(x), Some(y)) => x === y
  //          case _                  => false
  //        }
  //    }

  // Some Data
}

object Data {
  val o1 = Order(23.2323, 34343.232)
  val s1 = Student(1, "Dev")

  val o2 = Order(2.2, 343.232)
  val s2 = Student(2, "Vinod")

  val o3 = Order(32.23, 44343.232)
  val s3 = Student(1, "Navneet")

  val empl = Employee(1, "Navneet", "navneet@navneet.com")
}

object CustomEqInstances2 {
  import cats.instances.int._
  import cats.instances.double._
  import cats.syntax.eq._
  implicit val studentEq2: Eq[Student] = new Eq[Student] {
    def eqv(s1: Student, s2: Student): Boolean = s1.id === s2.id
  }
  implicit val orderEq2: Eq[Order] = new Eq[Order] {
    def eqv(o1: Order, o2: Order): Boolean = o1.quantity === o2.quantity && o1.amount === o2.amount
  }

  // OptionEq can be used
  implicit def optionEq[A <: Eq[A]](implicit eqIns: Eq[A]): Eq[Option[A]] =
    new Eq[Option[A]] {
      def eqv(o1: Option[A], o2: Option[A]): Boolean =
        (o1, o2) match {
          case (Some(x), Some(y)) => x === y
          case _                  => false
        }
    }
}

object EqEx extends App {
  // Explaination See ShowEx

  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances._
  import Data._

  // Using scala native implicitly

  val intEqual = implicitly[Eq[Int]].eqv(123, 123)
  val intOEqual = implicitly[Eq[Option[Int]]].eqv(Some(123), Some(123))
  val intLEqual = implicitly[Eq[List[Int]]].eqv(List(123), List(123))
  val intNotEqual = implicitly[Eq[Int]].neqv(123, 12)
  val intONotEqual = implicitly[Eq[Option[Int]]].neqv(Some(123), Some(12))
  val intLNotEqual = implicitly[Eq[List[Int]]].neqv(List(123), List(12))

  val studentSame = implicitly[Eq[Student]].eqv(s1, s2)
  val studentSame2 = implicitly[Eq[Student]].eqv(s1, s3)
  val studentNotSame = implicitly[Eq[Student]].neqv(s2, s3)
  val studentOSame = implicitly[Eq[Option[Student]]].eqv(Some(s1), Some(s3))
  val studentLSame = implicitly[Eq[List[Student]]].eqv(List(s1), List(s3))
  val studentLSame2 = implicitly[Eq[List[Student]]].eqv(List(s1), List(s2, s3))

  val orderSame = implicitly[Eq[Order]].eqv(o1, o2)
  val orderSame2 = implicitly[Eq[Order]].eqv(o1, o3)
  val orderNotSame = implicitly[Eq[Order]].neqv(o1, o2)
  val orderOSame = implicitly[Eq[Option[Order]]].eqv(Some(o1), Some(o3))
  val orderLSame = implicitly[Eq[List[Order]]].eqv(List(o1), List(o3))
  val orderLSame2 = implicitly[Eq[List[Order]]].eqv(List(o1), List(o3, o2))

  println(intEqual)
  println(intOEqual)
  println(intLEqual)
  println(intNotEqual)
  println(intONotEqual)
  println(intLNotEqual)

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)
  // val intNotEqual = implicitly[Eq[Int]].neqv(123, "123")
}

object EqEx_ extends App {
  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances2._
  import Data._

  val studentSame = implicitly[Eq[Student]].eqv(s1, s2)
  val studentSame2 = implicitly[Eq[Student]].eqv(s1, s3)
  val studentNotSame = implicitly[Eq[Student]].neqv(s2, s3)
  val studentOSame = implicitly[Eq[Option[Student]]].eqv(Some(s1), Some(s3))
  val studentLSame = implicitly[Eq[List[Student]]].eqv(List(s1), List(s3))
  val studentLSame2 = implicitly[Eq[List[Student]]].eqv(List(s1), List(s2, s3))

  val orderSame = implicitly[Eq[Order]].eqv(o1, o2)
  val orderSame2 = implicitly[Eq[Order]].eqv(o1, o3)
  val orderNotSame = implicitly[Eq[Order]].neqv(o1, o2)
  val orderOSame = implicitly[Eq[Option[Order]]].eqv(Some(o1), Some(o3))
  val orderLSame = implicitly[Eq[List[Order]]].eqv(List(o1), List(o3))
  val orderLSame2 = implicitly[Eq[List[Order]]].eqv(List(o1), List(o3, o2))

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)
}

object EqEx1 extends App {
  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances._
  import Data._

  // Using Interface Methods
  val intEqual = Eq.apply[Int].eqv(123, 123)
  val intNotEqual = Eq.apply[Int].neqv(123, 12)
  // val intNotEqual = Eq.apply[Int].neqv(123, "123")
  val intOEqual = Eq.apply[Option[Int]].eqv(Some(123), Some(123))
  val intLEqual = Eq.apply[List[Int]].eqv(List(123), List(123))
  val intONotEqual = Eq.apply[Option[Int]].neqv(Some(123), Some(12))
  val intLNotEqual = Eq.apply[List[Int]].neqv(List(123), List(12))

  val studentSame = Eq.apply[Student].eqv(s1, s2)
  val studentSame2 = Eq.apply[Student].eqv(s1, s3)
  val studentNotSame = Eq.apply[Student].neqv(s2, s3)
  val studentOSame = Eq.apply[Option[Student]].eqv(Some(s1), Some(s3))
  val studentLSame = Eq.apply[List[Student]].eqv(List(s1), List(s3))
  val studentLSame2 = Eq.apply[List[Student]].eqv(List(s1), List(s2, s3))

  val orderSame = Eq.apply[Order].eqv(o1, o2)
  val orderSame2 = Eq.apply[Order].eqv(o1, o3)
  val orderNotSame = Eq.apply[Order].neqv(o1, o2)
  val orderOSame = Eq.apply[Option[Order]].eqv(Some(o1), Some(o3))
  val orderLSame = Eq.apply[List[Order]].eqv(List(o1), List(o3))
  val orderLSame2 = Eq.apply[List[Order]].eqv(List(o1), List(o3, o2))

  println(intEqual)
  println(intOEqual)
  println(intLEqual)
  println(intNotEqual)
  println(intONotEqual)
  println(intLNotEqual)

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)

}
object EqEx1_ extends App {
  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances2._
  import Data._

  val studentSame = Eq.apply[Student].eqv(s1, s2)
  val studentSame2 = Eq.apply[Student].eqv(s1, s3)
  val studentNotSame = Eq.apply[Student].neqv(s2, s3)
  val studentOSame = Eq.apply[Option[Student]].eqv(Some(s1), Some(s3))
  val studentLSame = Eq.apply[List[Student]].eqv(List(s1), List(s3))
  val studentLSame2 = Eq.apply[List[Student]].eqv(List(s1), List(s2, s3))

  val orderSame = Eq.apply[Order].eqv(o1, o2)
  val orderSame2 = Eq.apply[Order].eqv(o1, o3)
  val orderNotSame = Eq.apply[Order].neqv(o1, o2)
  val orderOSame = Eq.apply[Option[Order]].eqv(Some(o1), Some(o3))
  val orderLSame = Eq.apply[List[Order]].eqv(List(o1), List(o3))
  val orderLSame2 = Eq.apply[List[Order]].eqv(List(o1), List(o3, o2))

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)
}
object EqEx2 extends App {
  import cats.instances.int._
  import cats.syntax.eq._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances._
  import Data._

  // Using Interface Syntaxes
  val intEqual: Boolean = 123 === 123
  val intNotEqual = 123 =!= 12
  val intOEqual = (Some(123): Option[Int]) === (Some(123): Option[Int])
  val intONotEqual = (Some(123): Option[Int]) =!= (Some(12): Option[Int])
  val intLEqual = List(123) === List(123)
  val intLEqual2 = List(1, 2, 3) === List(12, 3)
  //val intLEqual3 = List(1, 2, 3) === List(12, "3")
  val intLNotEqual = List(123) =!= List(12)
  // val intNotEqual = 123 === "123"

  val studentSame = s1 === s2
  val studentSame2 = s1 === s3
  val studentNotSame = s2 =!= s3
  val studentOSame = (Some(s1): Option[Student]) === (Some(s3): Option[Student])
  val studentLSame = List(s1) === List(s3)
  val studentLSame2 = List(s1) === List(s2, s3)

  val orderSame = o1 === o2
  val orderSame2 = o1 === o3
  val orderNotSame = o1 =!= o2
  val orderOSame = (Some(o1): Option[Order]) === (Some(o3): Option[Order])
  val orderLSame = List(o1) === List(o3)
  val orderLSame2 = List(o1) === List(o3, o2)

  println(intEqual)
  println(intOEqual)
  println(intLEqual)
  println(intLEqual2)
  println(intNotEqual)
  println(intONotEqual)
  println(intLNotEqual)

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)

}

object EqEx2_ extends App {
  import cats.instances.int._
  import cats.instances.option._
  import cats.instances.list._
  import CustomEqInstances2._
  import Data._
  import cats.syntax.eq._

  val studentSame = s1 === s2
  val studentSame2 = s1 === s3
  val studentNotSame = s2 =!= s3
  val studentOSame = (Some(s1): Option[Student]) === (Some(s3): Option[Student])
  val studentLSame = List(s1) === List(s3)
  val studentLSame2 = List(s1) === List(s2, s3)

  val orderSame = o1 === o2
  val orderSame2 = o1 === o3
  val orderNotSame = o1 =!= o2
  val orderOSame = (Some(o1): Option[Order]) === (Some(o3): Option[Order])
  val orderLSame = List(o1) === List(o3)
  val orderLSame2 = List(o1) === List(o3, o2)

  println(studentSame)
  println(studentSame2)
  println(studentNotSame)
  println(studentOSame)
  println(studentLSame)
  println(studentLSame2)
  println(orderSame)
  println(orderSame2)
  println(orderNotSame)
  println(orderOSame)
  println(orderLSame)
  println(orderLSame2)

}
