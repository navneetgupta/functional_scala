package com.navneetgupta.shapeless

import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr}

object DeriveInstancesForCoProducts  extends App {


  sealed trait Shape
  case class Rectangle(length: Int, width: Int) extends Shape
  case class Circle(radius: Int) extends Shape
  case class Cylinder(radius: Int, length: Int) extends Shape

  val shapeGen = Generic[Shape]

  implicit val cnilEncoder: CsvEncoder[CNil] = CsvEncoder.createEncoder(cnil => throw new Exception("In-Convicable"))

  implicit def coproductEncoder[H, T <: Coproduct](implicit
                                               hEncoder: CsvEncoder[H],
                                               tEncoder: CsvEncoder[T]
                                              ): CsvEncoder[H :+: T] = CsvEncoder.createEncoder {
    case Inl(h) => hEncoder.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }

  implicit val hnilEncoder: CsvEncoder[HNil] = CsvEncoder.createEncoder(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit
                                           hEncoder: CsvEncoder[H],
                                           tEncoder: CsvEncoder[T]
                                          ): CsvEncoder[H :: T] = CsvEncoder.createEncoder {
    case h :: t => hEncoder.encode(h) ++ tEncoder.encode(t) // :: Should be from hList not the list Cons operator.
  }

  implicit def genericEncoder[A, R](implicit
                                    gen : Generic.Aux[A, R], // object Generic { type Aux[A, R] = Generic[A] { type Repr = R}}
                                    enc : CsvEncoder[R]
                                   ): CsvEncoder[A] = CsvEncoder.createEncoder(a => enc.encode(gen.to(a)))

  val list: List[Shape] = List(Rectangle(5,6), Circle(4), Cylinder(4,10))

  import CsvEncoderInstances._

  println(writeCSV(list))



}
