package com.navneetgupta.scala.jdgoes

import scala.util.Try

object FPMax {
  def main(args: Array[String]): Unit = {
    println("Hey There!! Want To Play the Game? Let's Start By Entering Your Name?")
    val name = scala.io.StdIn.readLine()

    println(s"Hello Dear ${name}, Lets Start the Game.")

    var exec = true

    while (exec) {
      val num = scala.util.Random.nextInt(5) + 1

      println(s"Dear ${name}, Please Guess the number from 1 to 5")
      val guess = scala.io.StdIn.readLine().toInt

      if (guess == num) println(s"You Guessed Right, ${name} !")
      else println(s"You Guessed wrong ${name} ! The number was: ${num}")

      println(s"Do You Want to Continue Playing, Dear ${name}? ")

      scala.io.StdIn.readLine() match {
        case "y" => exec = true
        case "n" => exec = false
      }
    }
  }
}


object FPMax2 {

  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  trait Program[F[_]] {
    def finish[A](a: => A): F[A]
    def chain[A, B](fa: F[A], afb: A => F[B]): F[B]
    def map[A,B](fa: F[A], ab: A => B): F[B]
  }

  object Program {
    def apply[F[_]](implicit F: Program[F]): Program[F]= F
  }

  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](ab: A => B)(implicit P: Program[F]): F[B] = P.map(fa, ab)
    def flatMap[B](afb: A => F[B])(implicit P: Program[F]): F[B] = P.chain(fa, afb)
  }

  def finish[F[_],A](a: => A)(implicit P: Program[F]): F[A] = P.finish(a)

  trait Console[F[_]] {
    def putStrLn(line: String): F[Unit]
    def getStrLn(): F[String]
  }

  object Console {
    def apply[F[_]](implicit F: Console[F]): Console[F]= F
  }
  def putStrLn[F[_]: Console](line: String): F[Unit] = Console[F].putStrLn(line)
  def getStrLn[F[_]: Console](): F[String] = Console[F].getStrLn()


  trait Random[F[_]] {
    def nextInt(maxN: Int): F[Int]
  }
  object Random {
    def apply[F[_]](implicit F: Random[F]): Random[F]= F
  }

  def nextInt[F[_]: Random](maxN: Int) = Random[F].nextInt(maxN)

  case class IO[A](unsafeRun: () =>  A) { self =>
    def map[B](f: A => B): IO[B] = IO( () => f(self.unsafeRun()))
    def flatMap[B](f: A => IO[B]): IO[B] = IO( () => f(self.unsafeRun()).unsafeRun())
  }

  object IO {
    def point[A](a: => A): IO[A] = IO(() => a)

    implicit val ProgramIO = new Program[IO] {
      def finish[A](a: => A): IO[A] = IO.point(a)
      def chain[A, B](fa: IO[A], afb: A => IO[B]): IO[B] = fa.flatMap(afb)
      def map[A,B](fa: IO[A], ab: A => B): IO[B] = fa.map(ab)
    }

    implicit val ConsoleIO = new Console[IO] {
      def putStrLn(line: String): IO[Unit] = IO(() => println(line))
      def getStrLn(): IO[String] = IO(() => scala.io.StdIn.readLine())
    }

    implicit val RandomIO = new Random[IO] {
      def nextInt(maxN: Int): IO[Int]= IO.point(scala.util.Random.nextInt())
    }
  }
//
//  def putStrLn(line: String): IO[Unit] = IO(() => println(line))
//
//  def getStrLn(): IO[String] = IO(() => scala.io.StdIn.readLine())
//
//  def nextInt(maxN: Int): IO[Int] = IO.point(scala.util.Random.nextInt())

  def checkContinue[F[_]: Program: Console](name: String): F[Boolean] = {
    for {
      _     <- putStrLn(s"Do You Want to Contin ue Playing, Dear ${name}? ")
      input <- getStrLn().map(_.toLowerCase)
      cont     <- input match {
                  case "y"  => finish(true)
                  case "n"  => finish(false)
                  case _    => checkContinue(name)
                }
    } yield cont
  }

  def gameLoop[F[_]: Program: Random: Console](name: String): F[Unit] = {
    for {
      rand  <- nextInt(5).map(_ +1)
      _     <- putStrLn(s"Dear ${name}, Please Guess the number from 1 to 5")
      input <- getStrLn()
      _     <- parseInt(input).fold(
                  putStrLn("You didn't Enter a proper number")
                )( guess => {
                      if (guess == rand) putStrLn(s"You Guessed Right, ${name} !")
                      else putStrLn(s"You Guessed wrong ${name} ! The number was: ${guess}")
                })
      cont   <- checkContinue(name)
      _       <- if(cont) gameLoop(name) else  finish(())

    } yield ()
  }

  def main[F[_]: Program: Random: Console](args: Array[String]): F[Unit] = {
    for {
      _     <- putStrLn("Hey There!! Want To Play the Game? Let's Start By Entering Your Name?")
      name  <- getStrLn()
      _     <- putStrLn(s"Hello Dear ${name}, Lets Start the Game.")
      _     <- gameLoop(name)
    } yield()
  }
}
