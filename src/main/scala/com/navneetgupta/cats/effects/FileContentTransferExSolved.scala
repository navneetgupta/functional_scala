package com.navneetgupta.cats.effects

import java.io._

import cats.Monad
import cats.effect._
import cats.effect.concurrent.Semaphore
import cats.implicits._


object CommonModel {

  trait Transfer[F[_]] {
    def copy(source: File, dest: File)(implicit concurrent: Concurrent[F]): F[Long]
    def transfer(input: InputStream, output: OutputStream): F[Long]
  }

  //object Transfer {
  //  def apply[F[_]](implicit F: Transfer[F]): Transfer[F] = F
  //}

  trait Stream[F[_]] {
    def inputStream(f: File, guard: Semaphore[F]): Resource[F, FileInputStream]
    def outputStream(f: File, guard: Semaphore[F]): Resource[F, FileOutputStream]
    def inputOutputStream(
                           in: File,
                           out: File, guard: Semaphore[F]): Resource[F, (FileInputStream, FileOutputStream)]
  }

  object Stream {
    def apply[F[_]](implicit F: Stream[F]): Stream[F] = F
  }

  trait Console[F[_]] {
    def putStrLn(str: String): F[Unit]
    def readLn(): F[String]
  }

  object Console {
    def apply[F[_]](implicit F: Console[F]): Console[F] = F
  }

}

object FileContentTransferExSolved {
  def inputStream[F[_]: CommonModel.Stream](f: File, guard: Semaphore[F]): Resource[F, FileInputStream] =
    CommonModel.Stream[F].inputStream(f, guard)

  def outputStream[F[_]: CommonModel.Stream](f: File, guard: Semaphore[F]): Resource[F, FileOutputStream] =
    CommonModel.Stream[F].outputStream(f, guard)

  def inputOutputStream[F[_]: CommonModel.Stream](
      in: File,
      out: File, guard: Semaphore[F]): Resource[F, (FileInputStream, FileOutputStream)] =
    CommonModel.Stream[F].inputOutputStream(in, out, guard)

  def copy[F[_]: CommonModel.Transfer](source: File, dest: File)(implicit concurrent: Concurrent[F]): F[Long] =
    implicitly[CommonModel.Transfer[F]].copy(source, dest)
  // Above implicitly could be also used instead fo defining `def apply[F[_]](implicit F: Transfer[F]): Transfer[F] = F` in object and using Transfer[F].copy(source,dest)


  def transfer[F[_]: CommonModel.Transfer](input: InputStream,
                               output: OutputStream): F[Long] =
    implicitly[CommonModel.Transfer[F]].transfer(input, output)

  def putStrLn[F[_]: CommonModel.Console](line: String): F[Unit] = CommonModel.Console[F].putStrLn(line)

  def readLn[F[_]: CommonModel.Console](): F[String] = CommonModel.Console[F].readLn()

  def transmit(origin: InputStream,
               destination: OutputStream,
               buffer: Array[Byte],
               acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count <- if (amount > -1)
        IO(destination.write(buffer, 0, amount)) >> transmit(origin,
                                                             destination,
                                                             buffer,
                                                             acc + amount)
      else
        IO.pure(acc) // End of read stream reached (by java.io.InputStream contract), nothing to write
    } yield count // Returns the actual amount of bytes transmitted

  def main[F[_]: CommonModel.Stream: CommonModel.Transfer: Monad: CommonModel.Console](source: File,
                                                   dest: File)(implicit concurrent: Concurrent[F]): F[Unit] = {
    for {
      count <- FileContentTransferExSolved.copy(source, dest)
      _ <- putStrLn(
        s"$count bytes copied from ${source.getPath} to ${dest.getPath}")
    } yield ()
  }

}

object FileContentTransferExSolvedApp extends IOApp {

  implicit val StreamIO = new CommonModel.Stream[IO] {
    def inputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileInputStream] =
      Resource.make {
        IO(new FileInputStream(f)) // build
      } { inStream =>
        guard.withPermit {
          IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
        }
      }

    def outputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileOutputStream] =
      Resource.make {
        IO(new FileOutputStream(f)) // build
      } { outStream =>
        guard.withPermit {
          IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
        }
      }

    def inputOutputStream(
        in: File,
        out: File, guard: Semaphore[IO]): Resource[IO, (FileInputStream, FileOutputStream)] =
      for {
        inStream <- inputStream(in, guard)
        outStream <- outputStream(out, guard)
      } yield (inStream, outStream)

  }

  implicit val TransferIO = new CommonModel.Transfer[IO] {
    def copy(source: File, dest: File)(implicit concurrent: Concurrent[IO]): IO[Long] =
      for {
        guard <- Semaphore[IO](1)
        count <- FileContentTransferExSolved.inputOutputStream(source, dest, guard).use {
          case (in, out) =>
            guard.withPermit {
              transfer(in, out)
            }
        }
      } yield count


    def transfer(input: InputStream, output: OutputStream): IO[Long] =
      for {
        buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
        total <- FileContentTransferExSolved.transmit(input, output, buffer, 0L)
      } yield total
  }

  implicit val ConsoleIO = new CommonModel.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- if (args.length < 2)
        IO.raiseError(
          new IllegalArgumentException("Need origin and destination files"))
      else IO.unit
      orig = new File(args(0))
      dest = new File(args(1))
      _ <- FileContentTransferExSolved.main(orig, dest)
    } yield ExitCode.Success
}

// Run

// navneet_gupta> sbt
// sbt:navneet_gupta> runMain com.navneetgupta.cats.effects.FileContentTransferExSolvedApp build.sbt dest.txt


/**
  * Notes:
  *
  * IO is able to encapsulate side-effects, but the capacity to define concurrent and/or async and/or cancelable IO
  * instances comes from the existence of a Concurrent[IO] instance. Concurrent[F[_]] is a type class that, for an F
  * carrying a side-effect, brings the ability to cancel or start concurrently the side-effect in F.
  * */