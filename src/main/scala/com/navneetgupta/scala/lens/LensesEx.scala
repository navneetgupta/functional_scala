package com.navneetgupta.scala.lens

object LensesEx extends App {
  case class Point(x: Double, y: Double)
  case class Color(r: Byte, g: Byte, b: Byte)

  //case class Turtle(position: Point, heading: Double, color: Color)

  val turtle1 = Turtle(Point(10.0, 20.2), 2.23, Color(121.toByte, 145.toByte, 121.toByte))

  // To move a turtle
  case class Turtle(position: Point, heading: Double, color: Color) {
    def forward(dist: Double): Turtle = this.copy(position = position.copy(
      x = position.x + dist * math.cos(heading),
      y = position.y + dist * math.sin(heading)
    ))
  }

  /// for changing a position we need to copy the property for whole nested property.
  // for ex :
  /**
    * // imperative
    *   a.b.c.d.e += 1
    * // functional
    *   a.copy(
    *     b = a.b.copy(
    *       c = a.b.c.copy(
    *         d = a.b.c.d.copy(
    *         )))
    * */


}
