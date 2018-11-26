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
  implicit val stringEncoder: CsvEncoder[String] = CsvEncoder.createEncoder[String](_ :: Nil)

  implicit val intEncoder: CsvEncoder[Int] = CsvEncoder.createEncoder[Int](_.toString :: Nil)

  implicit val booleanEncoder: CsvEncoder[Boolean] = CsvEncoder.createEncoder[Boolean](_.toString :: Nil)

  def writeCSV[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map( value => enc.encode(value).mkString(",")).mkString("\n")
}
