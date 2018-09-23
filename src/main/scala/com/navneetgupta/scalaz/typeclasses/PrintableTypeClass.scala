package com.navneetgupta.scalaz.typeclasses

trait Printable[A] {
  def format(a: A): String
}

object PrintableInstnces {
  implicit val intPrintableInstances: Printable[Int] = new Printable[Int] {
    override def format(a: Int): String = a.toString()
  }

  implicit val stringPrintableInstances: Printable[String] = new Printable[String] {
    override def format(a: String): String = a
  }

  implicit val orderPrintableInstances: Printable[Order] = new Printable[Order] {
    def format(a: Order): String = s"Order[quantity = ${a.quantity}, amount = ${a.amount}]"
  }

  implicit def optionInstances[A](implicit printable: Printable[A]): Printable[Option[A]] =
    new Printable[Option[A]] {
      def format(a: Option[A]): String = a match {
        case Some(x) => printable.format(x)
        case _       => ""
      }
    }
}

object PrintableApp extends App {
  import PrintableInstnces._

  println(implicitly[Printable[Order]].format(Order(22.232, 43423.23)))
  println(implicitly[Printable[Option[Order]]].format(Some(Order(22.232, 43423.23))))
  println(implicitly[Printable[Option[Order]]].format(None))

  println(implicitly[Printable[Int]].format(1))
  println(implicitly[Printable[Option[Int]]].format(Some(1)))
  println(implicitly[Printable[Option[Int]]].format(None))

  println(implicitly[Printable[String]].format("Navneet gupta"))
  println(implicitly[Printable[Option[String]]].format(Some("Navneet Gupta")))
  println(implicitly[Printable[Option[String]]].format(None))
}

/**
 * Interface Methods for Printable
 */

object Printable {
  def toString[A](a: A)(implicit m: Printable[A]) = m.format(a)
}

object PrintableApp2 extends App {
  import PrintableInstnces._

  println(Printable.toString(Order(22.232, 43423.23)))
  println(Printable.toString[Option[Order]](Some(Order(22.232, 43423.23))))
  println(Printable.toString[Option[Order]](None))

  println(Printable.toString(1))
  println(Printable.toString[Option[Int]](Some(1)))
  println(Printable.toString[Option[Int]](None))

  println(Printable.toString("Navneet Gupta"))
  println(Printable.toString[Option[String]](Some("Navneet Gupta")))
  println(Printable.toString[Option[String]](None))
}

/**
 * Interface Syntax
 */

object PrintbaleSyntax {
  implicit class printableSyntax[A](a: A) {
    def print(implicit p: Printable[A]): String = p.format(a)
  }
  implicit class optionPrintableSyntax[A](a: Option[A]) {
    def print(implicit p: Printable[A]): String = a match {
      case Some(x) => p.format(x)
      case _       => ""
    }
  }
}

object PrintableApp3 extends App {
  import PrintableInstnces._
  import PrintbaleSyntax._

  println(Order(22.232, 43423.23).print)
  println(Some(Order(22.232, 43423.23)).print)
  val a: Option[Order] = None
  println(a.print)

  println(1.print)
  println(Some(1).print)
  val b: Option[Int] = None
  println(b.print)

  println("Navneet Gupta".print)
  println(Some("Navneet Gupta").print)
  val c: Option[String] = None
  println(c.print)

}
