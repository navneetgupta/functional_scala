package com.navneetgupta.scalaz.zio

import scalaz.zio.{App, IO}
import scalaz.zio.console._
import java.io.IOException

object FirstApp extends App {

  def run(args: List[String]) =
    myAppLogic.fold(_ => 1, _ => 0)

  def myAppLogic =
    for {
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- putStrLn(s"Hello, ${n}, good to meet you!")
    } yield ()
}
