package com.navneetgupta.cats.effects

import cats.effect._
import cats.implicits._

object ExternalInteractionWithIO extends IOApp {
  val program: IO[Unit] =
    for {
      _ <- IO(println("Enter Your Name: "))
      name <- IO(scala.io.StdIn.readLine)
      _ <- IO(println(s"Hello Dear $name"))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- program
    } yield ExitCode.Success
  // Problem :: How to test `program` written is behaving properly or not. Until we run it we cannnot test it.
  // Soultion Approach Tagless Final

}

object Common {
  trait Console[F[_]] {
    def putStrLn(str: String): F[Unit]
    def readLn(): F[String]
  }

  object Console {
    def apply[F[_]](implicit F: Console[F]): Console[F] = F
  }

  def putStrLn[F[_]: Console](line: String): F[Unit] = Console[F].putStrLn(line)

  def readLn[F[_]: Console](): F[String] = Console[F].readLn()

}
//
object TaglessSupportedEx {

  import Common._
  import cats.Monad

  def program[F[_]: Console : Monad]: F[Unit] =
    for {
      _ <- putStrLn("Enter Your Name: ")
      name <- readLn
      _ <- putStrLn(s"Hello Dear $name")
    } yield ()
}

object StdConsoleApp extends IOApp {
  import Common._

  implicit val ConsoleIO = new Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
    _ <- TaglessSupportedEx.program[IO]
  } yield ExitCode.Success
}

object TaglessSupportEx2 extends IOApp {
  import cats.Monad

  trait Console[F[_]] {
    def putStrLn(str: String): F[Unit]
    def readLn(): F[String]
  }

  def program[F[_]: Monad](implicit C: Console[F]): F[Unit] =
    for {
      _ <- C.putStrLn("Enter Your Name: ")
      name <- C.readLn
      _ <- C.putStrLn(s"Hello Dear $name")
    } yield ()

  class StdConsole[F[_]: Sync] extends Console[F] {
    override def putStrLn(str: String): F[Unit] = Sync[F].delay(println(str))

    override def readLn(): F[String] = Sync[F].delay(scala.io.StdIn.readLine)
  }

  implicit val ConsoleIO =  new StdConsole[IO]
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- program[IO]
    } yield ExitCode.Success

}
