package com.navneetgupta.scalaz.zio

import scalaz.zio.IO

object LiftPureValues extends  App {
  val p: IO[Nothing, String] = IO.point("Hello World!!!!")

  //  The constructor uses non-strict evaluation, so the parameter will not be evaluated until
  //  when and if the IO action is executed at runtime, which is useful if the construction is
  //  costly and the value may never be needed.

  //  Alternately, you can use the IO.now constructor to perform strict evaluation of the value:

  val p1 = IO.now("Hello World Strictly!!!!!!!")
  println(p)
  println(p1)
}
