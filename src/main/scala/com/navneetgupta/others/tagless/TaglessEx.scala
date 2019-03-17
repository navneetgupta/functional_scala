package com.navneetgupta.others.tagless

import cats.effect.IO

object TaglessEx {

  trait CMonad[F[_]] {
    def pure[A](a: A): F[A]

    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]

    def map[A, B](value: F[A])(func: A => B): F[B] =
      flatMap(value)(x => pure(func(x)))
  }

  object CMonad {
    def apply[F[_]](implicit F: CMonad[F]): CMonad[F] = F
  }

  implicit class CMoandSyntax[F[_], A](fa: F[A]) {
    def map[B](ab: A => B)(implicit P: CMonad[F]): F[B] = P.map(fa)(ab)
    def flatMap[B](ab: A => F[B])(implicit P: CMonad[F]): F[B] = P.flatMap(fa)(ab)
  }

  trait Console[F[_]] {
    def putStrLn(msg: String): F[Unit]

    def getStrLn: F[String]
  }

  object Console {
    def apply[F[_]](implicit console: Console[F]): Console[F] = console
  }

  def program[F[_] : CMonad : Console]: F[Unit] =
    for {
      _ <- Console[F].putStrLn("Good morning, what's your name?")
      name <- Console[F].getStrLn
      _ <- Console[F].putStrLn(s"Great to meet you, $name")
    } yield ()
}

object Program1 extends App {

  import TaglessEx._

  class IOCMonad extends CMonad[IO] {
    override def pure[A](a: A): IO[A] = IO.pure(a)

    override def flatMap[A, B](value: IO[A])(func: A => IO[B]): IO[B] = value.flatMap(func)
  }

  class ConsoleIO extends Console[IO] {
    def putStrLn(msg: String): IO[Unit] = IO(scala.Console.println(msg))

    def getStrLn: IO[String] = IO(scala.io.StdIn.readLine)
  }

  implicit val console: Console[IO] = new ConsoleIO
  implicit val iOCMonad: CMonad[IO] = new IOCMonad

  program.unsafeRunSync()
}

object Program1Test extends App {

  import TaglessEx._

  case class TestData(input: List[String], output: List[String]) {

    def putStrLn(line: String): (TestData, Unit) = (copy(output = line :: output), Unit)
    def getStrLn: (TestData, String) = (copy(input = input.drop(1)), input.head)

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
    def putStrLn(msg: String): TestIO[Unit] = TestIO(t => t.putStrLn(msg))

    def getStrLn: TestIO[String] = TestIO(t => t.getStrLn)
  }

  class TestIOMonad extends CMonad[TestIO]{
    override def flatMap[A, B](fa: TestIO[A])(f: A => TestIO[B]): TestIO[B] = fa.flatMap(f)

    override def map[A, B](fa: TestIO[A])(f: A => B): TestIO[B] = fa.map(f)
    override def pure[A](x: A): TestIO[A] = TestIO.value(x)
  }

  implicit val consoleTestIO : Console[TestIO] = new ConsoleTestIO
  implicit val testIOMonad: CMonad[TestIO] = new TestIOMonad

  def programTestIO: TestIO[Unit] = program[TestIO]

  val testData = TestData(List("Navneet Gupta"), Nil)
  println(programTestIO.eval(testData).showResults)
}


