package com.navneetgupta.kittens

import cats.Show
import com.navneetgupta.common._
import cats.derived
import cats.implicits._

object Data {
  val o1 = Order(23.2323, 34343.232)
  val s1 = Student(1, "Dev")

  val o2 = Order(2.2, 343.232)
  val s2 = Student(2, "Vinod")

  val o3 = Order(32.23, 44343.232)
  val s3 = Student(1, "Navneet")

  val empl = Employee(1, "Navneet", "navneet@navneet.com")
}

/**
  * Using `auto` type derivation
  */
object ShowEx extends App {

  import cats.instances.int._
  import cats.syntax.show._
  import cats.instances.option._
  import cats.instances.list._
  import derived.auto.show._

  //Auto derivation of instances using kittens library
  //  implicit val orderShow: Show[Order] = derived.semi.show
  //  implicit val studentShow: Show[Student] = derived.semi.show

  val intAsString: String = implicitly[Show[Int]].show(1)
  val orderAsString: String = implicitly[Show[Order]].show(Order(23.2323, 34343.232))
  val studentAsString: String = implicitly[Show[Student]].show(Student(1, "Navneet"))

  val intOAsString: String = implicitly[Show[Option[Int]]].show(Some(1))
  val orderOAsString: String = implicitly[Show[Option[Order]]].show(Some(Order(23.2323, 34343.232)))
  val studentOAsString: String = implicitly[Show[Option[Student]]].show(Some(Student(1, "Dev")))

  // val employeeOAsString: String = implicitly[Show[Option[Employee]]].show(Some(Employee(1, "Abhilash", "abhilash@gmail.com")))

  val intLAsString: String = implicitly[Show[List[Int]]].show(List(1, 2, 3))
  val orderLAsString: String = implicitly[Show[List[Order]]].show(List(Order(23.2323, 34343.232), Order(2.222, 432.232)))
  val studentLAsString: String = implicitly[Show[List[Student]]].show(List(Student(1, "Vinod"), Student(2, "Dev")))

  println(intAsString)
  println(orderAsString)
  println(studentAsString)
  println(intOAsString)
  println(orderOAsString)
  println(studentOAsString)
  println(intLAsString)
  println(orderLAsString)
  println(studentLAsString)
}

/**
  * Using `semi` type derivation
  */
object ShowEx2 extends App {

  import cats.instances.int._
  import cats.syntax.show._
  import cats.instances.option._
  import cats.instances.list._
  //import derived.auto.show._

  //Auto derivation of instances using kittens library
  implicit val orderShow: Show[Order] = derived.semi.show
  implicit val studentShow: Show[Student] = derived.semi.show

  val intAsString: String = implicitly[Show[Int]].show(1)
  val orderAsString: String = implicitly[Show[Order]].show(Order(23.2323, 34343.232))
  val studentAsString: String = implicitly[Show[Student]].show(Student(1, "Navneet"))

  val intOAsString: String = implicitly[Show[Option[Int]]].show(Some(1))
  val orderOAsString: String = implicitly[Show[Option[Order]]].show(Some(Order(23.2323, 34343.232)))
  val studentOAsString: String = implicitly[Show[Option[Student]]].show(Some(Student(1, "Dev")))

  // val employeeOAsString: String = implicitly[Show[Option[Employee]]].show(Some(Employee(1, "Abhilash", "abhilash@gmail.com")))

  val intLAsString: String = implicitly[Show[List[Int]]].show(List(1, 2, 3))
  val orderLAsString: String = implicitly[Show[List[Order]]].show(List(Order(23.2323, 34343.232), Order(2.222, 432.232)))
  val studentLAsString: String = implicitly[Show[List[Student]]].show(List(Student(1, "Vinod"), Student(2, "Dev")))

  println(intAsString)
  println(orderAsString)
  println(studentAsString)
  println(intOAsString)
  println(orderOAsString)
  println(studentOAsString)
  println(intLAsString)
  println(orderLAsString)
  println(studentLAsString)
}
