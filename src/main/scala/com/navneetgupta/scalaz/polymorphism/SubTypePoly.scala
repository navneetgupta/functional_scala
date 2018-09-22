package com.navneetgupta.scalaz.polymorphism

// Define a common plus method for diffrent type like String number list etc.

trait Plus[A] {
  def plus(a: A): A
}

case class Order(quantity: Double, amount: Double)

//object PlusInstances {
//  implicit val stringPlus: Plus[String] = new Plus[String] {
//    override def plus(a: List[String]) = a.foldLeft("")((a, b) => a.concat(b))
//  }
//  implicit val intPlus: Plus[Int] = new Plus[Int] {
//    override def plus(a: List[Int]) = a.foldLeft(0)(_ + _)
//  }
//  implicit val orderPlus: Plus[Order] = new Plus[Order] {
//    override def plus(a: List[Order]) = a.foldLeft(Order(0.0, 0.0))((a, b) => Order(a.quantity + b.quantity, a.amount + b.amount))
//  }
//}

object SubTypePoly extends App {
  //  import PlusInstances._
  //
  //  def plus[A <: Plus[A]](a1: A): A = a + a1
  //
  //  plus(List(1, 2, 3, 4))
}
