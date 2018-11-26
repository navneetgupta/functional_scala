package com.navneetgupta.shapeless

import shapeless.{:+:, CNil, Coproduct, Generic, Inl, Inr}
import CsvEncoderInstances._

object DeriveInstancesForCoProducts  extends App {

  sealed trait Shape
  case class Rectangle(length: Int, width: Int) extends Shape
  case class Circle(radius: Int) extends Shape
  case class Cylinder(radius: Int, length: Int) extends Shape

  val shapeGen = Generic[Shape]

  trait Area[A] {
    def area(a: A): Double
  }

  trait Perimeter[A] {
    def perimeter(a: A) : Double
  }

  implicit val chnilEncoder: CsvEncoder[CNil] = CsvEncoder.createEncoder(cnil => throw new Exception("InConvicable"))

  implicit val intEncoder: CsvEncoder[Int] = CsvEncoder.createEncoder[Int](_.toString :: Nil)

  implicit def clistEncoder[H, T <: Coproduct](implicit
                                           hEncoder: CsvEncoder[H],
                                           tEncoder: CsvEncoder[T]
                                          ): CsvEncoder[H :+: T] = CsvEncoder.createEncoder {
    case Inl(h) => hEncoder.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }

//  def writeCSV[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
//    values.map( value => enc.encode(value).mkString(",")).mkString("\n")

  val list: List[Shape] = List(Rectangle(5,6), Circle(4), Cylinder(4,10))

  println(writeCSV(list)(clistEncoder))




}
