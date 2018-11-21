package com.navneetgupta.shapeless

import shapeless._

object ProductsGenericSwitching extends App {
  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  val genericIceCream = Generic[IceCream]

  println(genericIceCream)

  val iceCream = IceCream("Sundae", 1, false)
  val repr = genericIceCream.to(iceCream)
  val iceCream2 = genericIceCream.from(repr)

  println(iceCream)
  println(repr)
  println(iceCream2)

  case class Employee(name: String, empId: Int, manager: Boolean)

  println(Generic[Employee].from(repr)) // Convert same Sturcture product(case class) to another product(case class)


  // Since Tuples are also generic product . ie. Tuples are actually Scala case classes. so Tuples can be generically represeted

  val tupleGen = Generic[(String, Int, Boolean)]

  println(tupleGen.to(("Sundae", 1, false)))
  println(tupleGen.from("Sundae":: 1 :: false :: HNil))

  println(genericIceCream.from(tupleGen.to(("Sundae", 1, false))))
  println(Generic[Employee].from(tupleGen.to(("Sundae", 1, false)))) // Tuple to case classes.


}
