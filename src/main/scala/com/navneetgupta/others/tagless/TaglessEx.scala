package com.navneetgupta.others.tagless

import cats.Monad
import cats.effect.IO
import cats.implicits._

object TaglessEx {

  trait Console[F[_]] {
    def putStrLn(msg: String): F[Unit]

    val getStrLn: F[String]
  }

  object Console {
    def apply[F[_]](implicit console: Console[F]): Console[F] = console
  }

  class ConsoleIO extends Console[IO] {
    override def putStrLn(msg: String): IO[Unit] = IO(scala.Console.println(msg))

    override val getStrLn: IO[String] = IO(scala.io.StdIn.readLine)
  }

  def program[F[_] : Console : Monad]: F[String] =
    for {
      _ <- Console[F].putStrLn("Good morning, what's your name?")
      name <- Console[F].getStrLn
      _ <- Console[F].putStrLn(s"Great to meet you, $name")
    } yield name

}


object Program1 extends App {

  import TaglessEx._

  implicit val console: Console[IO] = new ConsoleIO

  program.unsafeRunSync()
}

object Program1Test extends App {

  import TaglessEx._

  case class TestData(input: List[String], output: List[String]) {
    def putStrLn(line: String): (TestData, Unit) = (copy(output = line :: output), Unit)
    val getStrLn: (TestData, String) = (copy(input = input.drop(1)), input.head)

    def showResults = output.reverse.mkString("\n")
  }


  case class TestIO[A](run: TestData => (TestData, A)) { s =>
    def map[B](f: A => B): TestIO[B] = flatMap(a => TestIO.value(f(a)))
    def flatMap[B](f: A => TestIO[B]): TestIO[B] =
      TestIO(d =>
        (s run d) match { case (d, a) => f(a) run d })

    def eval(t: TestData): TestData = run(t)._1
  }
  object TestIO {
    def value[A](a: => A): TestIO[A] = TestIO(d => (d, a))
  }

  class ConsoleTestIO extends Console[TestIO] {
    override def putStrLn(msg: String): TestIO[Unit] = TestIO(t => t.putStrLn(msg))

    override val getStrLn: TestIO[String] = TestIO(t => t.getStrLn)
  }

  implicit val consoleTestIO : Console[TestIO] = new ConsoleTestIO

  def programTestIO = program[TestIO]
}
