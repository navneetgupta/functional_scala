package com.navneetgupta.cats.show

import cats.Show
import com.navneetgupta.common._

object CustomShowInstances {

  /**
    * Show is Cats’ mechanism for producing developer-friendly console output without using toString.
    */

  /**
    * Type Class For Show is as follow:
    *
    * trait Show[A] {
    * def show(value: A): String
    * }
    */

  /**
    * For Using any TypeClass we need instances for types for whihc we need to use the typeClass.
    *
    * Most of Instances defined in cats.implicts._ packet.
    * So we will import for Int
    * and define one custome instance for Student and Order
    */

  // Type Class Instance For Int ALready defined in package `cats.instances.int` so importing as it
  import cats.instances.int

  implicit val studentShow: Show[Student] = new Show[Student] {
    override def show(s: Student): String = s"Student[id = ${s.id}, name= ${s.name}]"
  }

  // Type class Instance for Order
  implicit val orderShow: Show[Order] = new Show[Order] {
    override def show(o: Order): String = s"Order[quantity = ${o.quantity}, amount= ${o.amount}]"
  }
}

object ShowEx extends App {

  import cats.instances.int._ // for int implicit already defined
  import cats.instances.option._ // provided for Option Type Option Type will work only if Option[A] A's Implicit is present already.
  // Like suppose we try to use Option[Employee] this should not work since we donot have implict of Employee
  //Similarly for List Also we have instances
  import cats.instances.list._

  /**
    * Uses Using Scala implicitly
    */

  import CustomShowInstances._

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

  // val employeeLAsString: String = implicitly[Show[List[Employee]]].show(List(Employee(1, "Abhilash", "abhilash@gmail.com")))

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

object ShowEx1 extends App {

  import cats.instances.int._
  import cats.instances.option._ // provided for Option Type Option Type will work only if Option[A] A's Implicit is present already.
  // Like suppose we try to use Option[Employee] this should not work since we donot have implict of Employee
  //Similarly for List Also we have instances
  import cats.instances.list._

  /**
    * Uses Interface methods provided by Show
    */
  val showInt: Show[Int] = Show.apply[Int]

  val intAsString: String = showInt.show(1)

  import CustomShowInstances._

  val showOrder: Show[Order] = Show.apply[Order]
  val orderAsString: String = showOrder.show(Order(23.2323, 34343.232))

  val showStudent: Show[Student] = Show.apply[Student]
  val studentAsString: String = showStudent.show(Student(1, "Dev"))

  val intOAsString: String = Show.apply[Option[Int]].show(Some(1))
  val orderOAsString: String = Show.apply[Option[Order]].show(Some(Order(23.2323, 34343.232)))
  val studentOAsString: String = Show.apply[Option[Student]].show(Some(Student(1, "Dev")))

  // val employeeOAsString: String = Show.apply[Option[Employee]].show(Some(Employee(1, "Abhilash", "abhilash@gmail.com")))

  val intLAsString: String = Show.apply[List[Int]].show(List(1, 2, 3))
  val orderLAsString: String = Show.apply[List[Order]].show(List(Order(23.2323, 34343.232), Order(2.222, 432.232)))
  val studentLAsString: String = Show.apply[List[Student]].show(List(Student(1, "Vinod"), Student(2, "Navneet")))

  // val employeeLAsString: String = Show.apply[List[Employee]].show(List(Employee(1, "Abhilash", "abhilash@gmail.com")))

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

object ShowEx2 extends App {

  import cats.instances.int._
  import cats.syntax.show._
  import cats.instances.option._ // provided for Option Type Option Type will work only if Option[A] A's Implicit is present already.
  // Like suppose we try to use Option[Employee] this should not work since we donot have implict of Employee
  //Similarly for List Also we have instances
  import cats.instances.list._

  /**
    * Uses Interface Syntaxes provided by imports cats.syntax.show._
    */

  val intAsString: String = 1.show

  import CustomShowInstances._

  val orderAsString: String = Order(23.2323, 34343.232).show
  val studentAsString: String = Student(1, "Vinod").show

  val intOAsString: String = Option[Int](1).show
  val orderOAsString: String = Option[Order](Order(23.2323, 34343.232)).show
  val studentOAsString: String = Option[Student](Student(1, "Vinod")).show

  // val employeeOAsString: String = Show.apply[Option[Employee]].show(Some(Employee(1, "Abhilash", "abhilash@gmail.com")))

  val intLAsString: String = List(1, 2, 3).show
  val orderLAsString: String = List(Order(23.2323, 34343.232), Order(2.222, 432.232)).show
  val studentLAsString: String = List(Student(1, "Navneet"), Student(2, "Dev")).show

  // val employeeLAsString: String = Show.apply[List[Employee]].show(List(Employee(1, "Abhilash", "abhilash@gmail.com")))

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
