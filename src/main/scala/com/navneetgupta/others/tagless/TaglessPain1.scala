package com.navneetgupta.others.tagless

import cats.effect.IO

object TaglessPain1 {

  trait Console {
    def putStrLn(msg: String): IO[Unit]

    def getStrLn: IO[String]
  }


  def program(c: Console): IO[Unit] =
    for {
      _ <- c.putStrLn("Good morning, what's your name?")
      name <- c.getStrLn
      _ <- c.putStrLn(s"Great to meet you, $name")
    } yield ()
}

object TaglessPain1Program extends App {
  import TaglessPain1._

  class ConsoleInst extends Console {
    override def putStrLn(msg: String): IO[Unit] = IO(scala.Console.println(msg))

    override def getStrLn: IO[String] = IO(scala.io.StdIn.readLine)
  }

  def programIO = program(new ConsoleInst)

  programIO.unsafeRunSync()
}


