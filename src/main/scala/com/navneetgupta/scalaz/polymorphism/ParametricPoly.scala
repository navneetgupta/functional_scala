package com.navneetgupta.scalaz.polymorphism

object ParametricPoly extends App {
  def head[A](xs: List[A]): A = xs(0)

  println(head(List(Student(1, "Abhishek"), Student(2, "Abhilasha"), Student(3, "Lata"), Student(4, "Nayan"), Student(5, "Navneet"), Student(6, "Vinod"), Student(7, "Dev"))))
  println(head(List(1, 2, 3, 4, 5, 6)))
  println(head(List("One", "Two", "Three", "Four", "Five", "Six")))
}

case class Student(id: Int, name: String)

trait Algebra[A] {
  def head[A](xs: List[A]): A
}
