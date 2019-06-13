package com.navneetgupta.scalaz.zio

import java.io.{File, IOException}

import scalaz.zio.IO


object BracketsEx extends App {

  // Brackets are used for a similar purpose as try/catch/finally, only brackets work with synchronous and asynchronous actions,
  // work seamlessly with fiber interruption, and are built on a different error model that ensures no errors are ever swallowed.
  //
  //Brackets consist of an acquire action, a utilize action (which uses the acquired resource), and a release action


  // Release action is same as finally in Java. ie it would be executed in runtime even if there is some exceptions.


  val z: IO[IOException, Unit] = openFile("data.json").bracket(closeFile(_)) { file =>
    for {
      data <- decodeData(file)
      grouped <- groupData(data)
    } yield grouped
  }


  // Just for Satisfying the Types
  def openFile(fileName: String): IO[IOException, File] = ???

  def closeFile(file: File): IO[Nothing, Boolean] = ???

  def decodeData(file: File): IO[Nothing, String] = ???

  def groupData(value: String): IO[IOException, Unit] = ???


  // A helper method called ensuring provides a simpler analogue of finally:
  var i: Int = 0
  /**
    * A variant of `flatMap` that ignores the value produced by this action.
    *
    * final def *>[E1 >: E, B](io: => IO[E1, B]): IO[E1, B] = self.flatMap(_ => io)
    */

  val action: IO[Throwable, String] = IO.effectTotal(i += 1) *> IO.fail(new Throwable("Boom!"))
  val cleanupAction: IO[Nothing, Unit] = IO.effectTotal(i -= 1)
  val composite = action.ensuring(cleanupAction)


}

