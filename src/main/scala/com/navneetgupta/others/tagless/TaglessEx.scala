package com.navneetgupta.others.tagless

import cats.effect.IO

object TaglessEx {

  trait Console[F[_]] {
    def putStrLn(msg: String): F[Unit]

    val getStrLn: F[String]
  }

  trait CMonad[F[_]] {
    def pure[A](a: A): F[A]

    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]

    def map[A, B](value: F[A])(func: A => B): F[B] =
      flatMap(value)(x => pure(func(x)))
  }

  object CMonad {
    def apply[F[_]](implicit F: CMonad[F]): CMonad[F] = F
  }

  object Console {
    def apply[F[_]](implicit console: Console[F]): Console[F] = console
  }

  class ConsoleIO extends Console[IO] {
    override def putStrLn(msg: String): IO[Unit] = IO(scala.Console.println(msg))

    override val getStrLn: IO[String] = IO(scala.io.StdIn.readLine)
  }

  implicit class CMoandSyntax[F[_], A](fa: F[A]) {
    def map[B](ab: A => B)(implicit P: CMonad[F]): F[B] = P.map(fa)(ab)
    def flatMap[B](ab: A => F[B])(implicit P: CMonad[F]): F[B] = P.flatMap(fa)(ab)
  }

  def program[F[_] : Console : CMonad]: F[String] =
    for {
      _ <- Console[F].putStrLn("Good morning, what's your name?")
      name <- Console[F].getStrLn
      _ <- Console[F].putStrLn(s"Great to meet you, $name")
    } yield name

}

object Program1 extends App {

  import TaglessEx._

  class IOCMonad extends CMonad[IO] {
    override def pure[A](a: A): IO[A] = IO.pure(a)

    override def flatMap[A, B](value: IO[A])(func: A => IO[B]): IO[B] = value.flatMap(func)
  }

  implicit val console: Console[IO] = new ConsoleIO
  implicit val iOCMonad: CMonad[IO] = new IOCMonad

  program.unsafeRunSync()
}

object Program1Test extends App {

  import TaglessEx._

  case class TestData(input: List[String], output: List[String]) {
    def putStrLn(line: String): (TestData, Unit) = (copy(output = line :: output), Unit)
    val getStrLn: (TestData, String) = (copy(input = input.drop(1)), input.head)

    def showResults = output.reverse.mkString("\n")
  }

  case class TestIO[A](run: TestData => (TestData, A)) { self =>
    def map[B](ab: A => B): TestIO[B] = TestIO(t => self.run(t) match {
      case (t, a) => (t, ab(a))
    })

    def flatMap[B](afb: A => TestIO[B]): TestIO[B] = TestIO(t => self.run(t) match {
      case (t, a) => afb(a).run(t)
    })

    def eval(t: TestData): TestData = run(t)._1
  }

  object TestIO {
    def value[A](a: => A): TestIO[A] = TestIO(d => (d, a))
  }

  class ConsoleTestIO extends Console[TestIO] {
    override def putStrLn(msg: String): TestIO[Unit] = TestIO(t => t.putStrLn(msg))

    override val getStrLn: TestIO[String] = TestIO(t => t.getStrLn)
  }

  class TestIOMonad extends CMonad[TestIO]{
    override def flatMap[A, B](fa: TestIO[A])(f: A => TestIO[B]): TestIO[B] = fa.flatMap(f)

    override def map[A, B](fa: TestIO[A])(f: A => B): TestIO[B] = fa.map(f)
    override def pure[A](x: A): TestIO[A] = TestIO.value(x)
  }

  implicit val consoleTestIO : Console[TestIO] = new ConsoleTestIO
  implicit val testIOMonad: CMonad[TestIO] = new TestIOMonad


  def programTestIO: TestIO[String] = program[TestIO]


  val testData = TestData(List("Navneet Gupta"), Nil)
  programTestIO.eval(testData).showResults
}

