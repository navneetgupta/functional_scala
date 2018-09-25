package com.navneetgupta.kittens

import cats.Monoid
import cats.derived
import com.navneetgupta.common._
import cats.implicits._

object MonoidData {
  val o1 = Order(23.2323, 34343.232)
  val s1 = Student(1, "Dev")

  val o2 = Order(2.2, 343.232)
  val s2 = Student(2, "Vinod")

  val o3 = Order(32.23, 44343.232)
  val s3 = Student(1, "Navneet")

  val empl = Employee(1, "Navneet", "navneet@navneet.com")
}

object MonoidEx extends App {
  import cats.instances.option._
  import cats.instances.list._
  import derived.auto.monoid._
  import MonoidData._

  val orderMonoidValue = implicitly[Monoid[Order]].combine(o1, o2)
  val orderOMonoidValue = implicitly[Monoid[Option[Order]]].combine(Some(o1), Some(o2))
  val orderOMonoidValue2 = implicitly[Monoid[Option[Order]]].combine(Some(o1), None)
  val orderLMonoidValue = implicitly[Monoid[List[Order]]].combine(List(o1), List(o2))

  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx2 extends App {
  import cats.instances.option._
  import cats.instances.list._
  import MonoidData._

  implicit val orderMonoid: Monoid[Order] = derived.semi.monoid

  val orderMonoidValue = implicitly[Monoid[Order]].combine(o1, o2)
  val orderOMonoidValue = implicitly[Monoid[Option[Order]]].combine(Some(o1), Some(o2))
  val orderOMonoidValue2 = implicitly[Monoid[Option[Order]]].combine(Some(o1), None)
  val orderLMonoidValue = implicitly[Monoid[List[Order]]].combine(List(o1), List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx3 extends App {
  import cats.instances.option._
  import cats.instances.list._
  import derived.auto.monoid._
  import MonoidData._

  val orderMonoidValue = Monoid.apply[Order].combine(o1, o2)
  val orderOMonoidValue = Monoid.apply[Option[Order]].combine(Some(o1), Some(o2))
  val orderOMonoidValue2 = Monoid.apply[Option[Order]].combine(Some(o1), None)
  val orderLMonoidValue = Monoid.apply[List[Order]].combine(List(o1), List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx4 extends App {
  import cats.instances.option._
  import cats.instances.list._
  import MonoidData._

  implicit val orderMonoid: Monoid[Order] = derived.semi.monoid

  val orderMonoidValue = Monoid.apply[Order].combine(o1, o2)
  val orderOMonoidValue = Monoid.apply[Option[Order]].combine(Some(o1), Some(o2))
  val orderOMonoidValue2 = Monoid.apply[Option[Order]].combine(Some(o1), None)
  val orderLMonoidValue = Monoid.apply[List[Order]].combine(List(o1), List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx5 extends App {
  import cats.instances.option._
  import cats.instances.list._
  import derived.auto.monoid._
  import MonoidData._
  import cats.syntax.monoid._

  val orderMonoidValue = o1.combine(o2)
  val orderOMonoidValue = (Some(o1): Option[Order]).combine(Some(o2): Option[Order])
  val orderOMonoidValue2 = (Some(o1): Option[Order]).combine(None: Option[Order])
  val orderLMonoidValue = List(o1).combine(List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx6 extends App {
  import cats.instances.option._
  import cats.instances.list._
  import MonoidData._
  import cats.syntax.monoid._

  implicit val orderMonoid: Monoid[Order] = derived.semi.monoid

  val orderMonoidValue = o1.combine(o2)
  val orderOMonoidValue = (Some(o1): Option[Order]).combine(Some(o2): Option[Order])
  val orderOMonoidValue2 = (Some(o1): Option[Order]).combine(None: Option[Order])
  val orderLMonoidValue = List(o1).combine(List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}
