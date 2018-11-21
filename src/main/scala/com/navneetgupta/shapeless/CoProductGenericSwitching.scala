package com.navneetgupta.shapeless

import shapeless._

object CoProductGenericSwitching extends App {
  case class Red()
  case class Amber()
  case class Green()

  type TrafficLight  = Red :+: Amber :+: Green :+: CNil

  // :+: has two subtypes, Inl and Inr, that correspond loosely to Left and Right.

  val red: TrafficLight = Inl(Red())
  val amber: TrafficLight = Inr(Inl(Amber()))
  val green: TrafficLight = Inr(Inr(Inl(Green())))

  println(red)
  println(amber)
  println(green)

  // Every coproduct type is terminated with CNil, which is an empty type with no values, similar to Nothing.
  // We can’t instan􏰀ate CNil or build a Coproduct purely from instances of Inr. We always have exactly one Inl in a value.


  sealed trait Shape

  case class Rectangle(length: Int , width: Int) extends Shape
  case class Circle(radius: Int) extends Shape

  val shapeGen = Generic[Shape]

  println(shapeGen.to(Circle(10)))
  println(shapeGen.to(Rectangle(20 , 10)))
  println(shapeGen.from(shapeGen.to(Rectangle(20 , 10))))
  println(shapeGen.from(shapeGen.to(Circle(10))))
}
