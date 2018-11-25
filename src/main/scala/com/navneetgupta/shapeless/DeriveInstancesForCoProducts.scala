package com.navneetgupta.shapeless

import shapeless.Generic

object DeriveInstancesForCoProducts  extends App {
  sealed trait Shape
  case class Rectangle(length: Int, width: Int) extends Shape
  case class Circle(radius: Int) extends Shape
  case class Cyllinder(radius: Int, length: Int) extends Shape

  val shapeGen = Generic[Shape]


}
