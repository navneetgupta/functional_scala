package com.navneetgupta.cats.monoid

import com.navneetgupta.common._
import cats.Monoid

/**
 * Formally, a monoid for a type A is:
 *     • an operation `combine` with type (A,A ) => A
 *     • an element empty of type A
 *
 * For all values x, y, and z, in A, combine must be associative and empty must be an identity element
 *
 */

object Data {
  val o1 = Order(23.2323, 34343.232)
  val s1 = Student(1, "Dev")

  val o2 = Order(2.2, 343.232)
  val s2 = Student(2, "Vinod")

  val o3 = Order(32.23, 44343.232)
  val s3 = Student(1, "Navneet")

  val empl = Employee(1, "Navneet", "navneet@navneet.com")
}

object CustomMonoidInstances {
  implicit val orderMonoid = new Monoid[Order] {
    def combine(o1: Order, o2: Order): Order = Order(o1.quantity + o2.quantity, o1.amount + o2.amount)
    def empty: Order = Order(0.0, 0.0)
  }
  // We can define studentMonoid but it doesn't make any sense of combining student
}

object MonoidEx extends App {
  import CustomMonoidInstances._
  import Data._

  import cats.instances.option._
  import cats.instances.list._

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
  import CustomMonoidInstances._
  import Data._

  import cats.instances.option._
  import cats.instances.list._

  val orderMonoidValue = Monoid.apply[Order].combine(o1, o2)
  val orderOMonoidValue = Monoid.apply[Option[Order]].combine(Some(o1), Some(o2))
  val orderOMonoidValue2 = Monoid.apply[Option[Order]].combine(Some(o1), None)
  val orderLMonoidValue = Monoid.apply[List[Order]].combine(List(o1), List(o2))
  println(orderMonoidValue)
  println(orderOMonoidValue)
  println(orderOMonoidValue2)
  println(orderLMonoidValue)
}

object MonoidEx3 extends App {
  import CustomMonoidInstances._
  import Data._

  import cats.instances.option._
  import cats.instances.list._
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
