package com.navneetgupta.scalaz.polymorphism

import scalaz._
import Scalaz._

case class Order(quantity: Double, amount: Double)

object Order {
  implicit val orderEq: Equal[Order] =
    Equal[Order] { (o1, o2) =>
      (o1.quantity == o2.quantity) // Here we can modify the custom Comparator. Instead of default whihc compare whole Object
    }
}

object EqualApp extends App {
  val o1 = Order(10.22, 23.2)
  val o2 = Order(10.22, 23.232)
  val o3 = Order(2.23, 23.232)

  import Order._

  println(o1 === o2)
  println(o1 === o3)
  println(o1 =/= o2)
  println(o1 =/= o3)
}
