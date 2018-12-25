package com.navneetgupta.scalaz.zio

import scalaz.zio.console._
import scalaz.zio.IO

object BasicEx {

  def main2() =  {
    for {
      _ <- putStrLn("Please Enter Your Name")
      name <- getStrLn
      _ <- putStrLn(s"Welcome Dear $name")
    } yield()
  }

  def main(args: Array[String]) = {
    main2()
    ()
  }

  def runInParallel[E, A, B](
                            leftIO: IO[E, A],
                            rightIO: IO[E, B]
                            ) = {
    for {
      leftBinder <- leftIO.fork
      rightBinder <- rightIO.fork
      a <- leftBinder.join
      b <- rightBinder.join
    } yield (a,b)
  }
}
