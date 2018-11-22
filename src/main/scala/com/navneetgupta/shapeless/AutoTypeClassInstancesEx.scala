package com.navneetgupta.shapeless

object AutoTypeClassInstancesEx  extends App {
  // TypeClass encoded in Scala using traits and implicits. A type class is a parameterised
  // trait representing some sort of general functionality that we would like to apply to a wide range of types:

  trait CSVEncoder[A] {
    def encode(a: A): List[String]
  }

  case class Employee(name: String, age: Int, isManager: Boolean)

  implicit val employeeCSVEncoder: CSVEncoder[Employee] = new CSVEncoder[Employee] {
    override def encode(a: Employee): List[String] = List(a.name, a.age.toString, a.isManager.toString)
  }


  def writeCSV[A](values: List[A])(implicit enc: CSVEncoder[A]): String =
    values.map( value => enc.encode(value).mkString(",")).mkString("\n")

  // This behaviour, known as “implicit resolution”, is what makes the type class pattern so powerful in Scala.

  // Given a set of rules encoded as implicit vals and implicit defs, the compiler is capable of searching for combina􏰀ons to give it the required instance
  // For Ex. a pair of (Employee, IceCream) so if we have implicit vals for Employee and Icecream compiler will auto detect the implict for the pair.

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  implicit def pairInstance[A,B](implicit aEncoder: CSVEncoder[A], bEncoder: CSVEncoder[B]): CSVEncoder[(A,B)] = new CSVEncoder[(A,B)] {
    override def encode(a: (A, B)): List[String] = aEncoder.encode(a._1) ++ bEncoder.encode(a._2)
  }

  // Even with implicit resolution the compiler can’t pull apart our case classes and sealed traits.
  // We are required to define instances for ADTs by hand.
  // Shapeless’ generic representa􏰀ons change all of this, allowing us to derive instances for any ADT for free.


}
