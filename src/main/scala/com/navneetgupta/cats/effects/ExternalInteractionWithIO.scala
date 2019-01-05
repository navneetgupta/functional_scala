package com.navneetgupta.cats.effects

import cats.effect.{ExitCode, IO, IOApp}

object ExternalInteractionWithIO extends IOApp{
  val program : IO[Unit] =
    for {
      _ <- IO(println("Enter Your Name: "))
      name <- IO(scala.io.StdIn.readLine)
      _ <- IO(println(s"Hello Dear $name"))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] = for {
    _ <- program
  } yield ExitCode.Success
}
