package com.navneetgupta.scalaz.zio

import scalaz.zio.FiberLocal

object FibersLocalExApp extends App {

  val a  = for {
    local <- FiberLocal.make[Int]
    _ <- local.set(3)
    v <- local.get
    _ <- local.empty
  } yield v == Some(3)

  // always get value after setting it up other wise it would lead to memory leak
  // use locally instead to avoid the memory Leakage




  val b = for {
    local <- FiberLocal.make[Int]
    v = local.locally(10)(local.get)
  } yield v == Some(3)


  println(a)
  println(b)

}
