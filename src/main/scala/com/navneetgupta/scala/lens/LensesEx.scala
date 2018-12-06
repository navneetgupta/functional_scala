package com.navneetgupta.scala.lens

import scalaz._

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
    * b = a.b.copy(
    * c = a.b.c.copy(
    * d = a.b.c.d.copy(
    * )))
    **/


  // Lens in Scalaz
  //  type Lens[A, B] = LensT[Id, A, B]

  val turtlePosition = Lens.lensu[Turtle, Point](
    (a, value) => a.copy(position = value),
    _.position
  )

  val pointX = Lens.lensu[Point, Double](
    (a, value) => a.copy(x = value),
    _.x
  )

  val turtleX = turtlePosition >=> pointX

  println(turtleX.get(turtle1))
  println(turtleX.set(turtle1, 40.0))
  println(turtleX.get(turtleX.set(turtle1, 40.0)))
  println(turtleX.mod(_ + 1.23, turtleX.set(turtle1, 40.0)))

  val incX = turtleX =>= {
    _ + 1.0
  }

  println(incX(turtle1))

  val incXState = for { // State which is  S => (S,A)
    x <- turtleX %= {
      _ + 1.23
    }
  } yield x

  println(incXState(turtle1))

  val turtleHeading = Lens.lensu[Turtle, Double](
    (a, value) => a.copy(heading = value),
    _.heading
  )

  val pointY = Lens.lensu[Point, Double](
    (a, value) => a.copy(y = value),
    _.y
  )

  val turtleY = turtlePosition >=> pointY

  def forward(dist: Double) = for {
    heading <- turtleHeading
    x <- turtleX += dist * math.cos(heading)
    y <- turtleY += dist * math.sin(heading)
  } yield (x, y)

  println(forward(22.4)(turtle1))

  println(forward(22.4) exec(turtle1))




}


