package com.navneetgupta.others.tagless

import cats.effect.IO
import cats.Monad
import cats.implicits._

import scala.annotation.tailrec

object TaglessExAnother {

  trait Console[F[_]] {
    def putStrLn(msg: String): F[Unit]

    val getStrLn: F[String]
  }

  object Console {
    def apply[F[_]](implicit console: Console[F]): Console[F] = console
  }

  def program[F[_]: Console: Monad]: F[String] =
    for {
      _ <- Console[F].putStrLn("Good morning, what's your name?")
      name <- Console[F].getStrLn
      _ <- Console[F].putStrLn(s"Great to meet you, $name")
    } yield name

}

object Program1Another extends App {

  import TaglessExAnother._

  class ConsoleIO extends Console[IO] {
    override def putStrLn(msg: String): IO[Unit] =
      IO(scala.Console.println(msg))

    override val getStrLn: IO[String] = IO(scala.io.StdIn.readLine)
  }

  implicit val console: Console[IO] = new ConsoleIO

  program.unsafeRunSync()
}

object Program1TestAnother extends App {

  import TaglessExAnother._

  case class TestData(input: List[String], output: List[String]) {
    def putStrLn(line: String): (TestData, Unit) =
      (copy(output = line :: output), Unit)
    val getStrLn: (TestData, String) = (copy(input = input.drop(1)), input.head)

    def showResults = output.reverse.mkString("\n")
  }

  case class TestIO[A](run: TestData => (TestData, A)) { self =>
    def map[B](ab: A => B): TestIO[B] =
      TestIO(t =>
        self.run(t) match {
          case (t, a) => (t, ab(a))
      })

    def flatMap[B](afb: A => TestIO[B]): TestIO[B] =
      TestIO(t =>
        self.run(t) match {
          case (t, a) => afb(a).run(t)
      })

    def eval(t: TestData): TestData = run(t)._1
  }

  object TestIO {
    def value[A](a: => A): TestIO[A] = TestIO(d => (d, a))
  }

  class ConsoleTestIO extends Console[TestIO] {
    override def putStrLn(msg: String): TestIO[Unit] =
      TestIO(t => t.putStrLn(msg))

    override val getStrLn: TestIO[String] = TestIO(t => t.getStrLn)
  }

  class TestIOMonad extends Monad[TestIO] {
    override def flatMap[A, B](fa: TestIO[A])(f: A => TestIO[B]): TestIO[B] =
      fa.flatMap(f)

    override def map[A, B](fa: TestIO[A])(f: A => B): TestIO[B] = fa.map(f)

    override def pure[A](x: A): TestIO[A] = TestIO.value(x)

    final def tailRecM[A, B](a: A)(f: A => TestIO[Either[A, B]]): TestIO[B] =
      TestIO {
        t =>
          f(a).run(t) match {
            case (t1, Right(b1)) => (t1, b1)
            case (t1, Left(a1)) => tailRecM(a)(f).run(t1)
          }
      }


  }

  implicit val consoleTestIO: Console[TestIO] = new ConsoleTestIO
  implicit val testIOMonad: Monad[TestIO] = new TestIOMonad

  def programTestIO: TestIO[String] = program[TestIO]

  val testData = TestData(List("Navneet Gupta"), Nil)
  programTestIO.eval(testData).showResults
}
