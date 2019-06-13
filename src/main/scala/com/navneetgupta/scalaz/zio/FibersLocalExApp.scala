package com.navneetgupta.scalaz.zio

import scalaz.zio.{DefaultRuntime, FiberLocal}
import scalaz.zio.console._

object FibersLocalExApp extends App {

  val a = for {
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

  val rts = new DefaultRuntime {}

  rts.unsafeRun(
    for {
      a1 <- a
      b1 <- b
      _ <- putStrLn(s"a's Value is ${a1}")
      _ <- putStrLn(s"b's Value is ${b1}")
    } yield ()
  )

}
