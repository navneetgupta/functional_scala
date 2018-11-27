package com.navneetgupta.shapeless


trait CsvEncoder[A] {
  def encode(a: A) : List[String]
}

object CsvEncoder {
  def createEncoder[A](f: A => List[String]): CsvEncoder[A] = new CsvEncoder[A] {
    override def encode(a: A): List[String] = f(a)
  }
}

object CsvEncoderInstances {
  import CsvEncoder._

  implicit val stringEncoder: CsvEncoder[String] = createEncoder[String](_ :: Nil)

  implicit val intEncoder: CsvEncoder[Int] = createEncoder[Int](_.toString :: Nil)

  implicit val booleanEncoder: CsvEncoder[Boolean] = createEncoder[Boolean](_.toString :: Nil)

  def writeCSV[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map( value => enc.encode(value).mkString(",")).mkString("\n")
}
