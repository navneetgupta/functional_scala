package com.navneetgupta.cats.effects

import java.io._

import cats.effect._
import cats.implicits._
import cats.Monad

trait Transfer[F[_]] {
  def copy(source: File, dest: File): F[Long]
  def transfer(input: InputStream, output: OutputStream): F[Long]
}

//object Transfer {
//  def apply[F[_]](implicit F: Transfer[F]): Transfer[F] = F
//}

trait Stream[F[_]] {
  def inputStream(f: File): Resource[F, FileInputStream]
  def outputStream(f: File): Resource[F, FileOutputStream]
  def inputOutputStream(
      in: File,
      out: File): Resource[F, (FileInputStream, FileOutputStream)]
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

object FileContentTransferEx {
  def inputStream[F[_]: Stream](f: File): Resource[F, FileInputStream] =
    Stream[F].inputStream(f)

  def outputStream[F[_]: Stream](f: File): Resource[F, FileOutputStream] =
    Stream[F].outputStream(f)

  def inputOutputStream[F[_]: Stream](
      in: File,
      out: File): Resource[F, (FileInputStream, FileOutputStream)] =
    Stream[F].inputOutputStream(in, out)

  def copy[F[_]: Transfer](source: File, dest: File): F[Long] =
    implicitly[Transfer[F]].copy(source, dest)
  // Above implicitly could be also used instead fo defining `def apply[F[_]](implicit F: Transfer[F]): Transfer[F] = F` in object and using Transfer[F].copy(source,dest)


  def transfer[F[_]: Transfer](input: InputStream,
                               output: OutputStream): F[Long] =
    implicitly[Transfer[F]].transfer(input, output)

  def putStrLn[F[_]: Console](line: String): F[Unit] = Console[F].putStrLn(line)

  def readLn[F[_]: Console](): F[String] = Console[F].readLn()

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

  def main[F[_]: Stream: Transfer: Monad: Console](source: File,
                                                   dest: File): F[Unit] = {
    for {
      count <- FileContentTransferEx.copy(source, dest)
      _ <- putStrLn(
        s"$count bytes copied from ${source.getPath} to ${dest.getPath}")
    } yield ()
  }

}

object ExampleApp extends IOApp {

  implicit val StreamIO = new Stream[IO] {
    def inputStream(f: File): Resource[IO, FileInputStream] =
      Resource.make {
        IO(new FileInputStream(f)) // build
      } { inStream =>
        IO(inStream.close()).handleErrorWith( _ => IO.unit) // release
      }

    def outputStream(f: File): Resource[IO, FileOutputStream] =
      Resource.make {
        IO(new FileOutputStream(f)) // build
      } { outStream =>
        IO(outStream.close()).handleErrorWith( _ => IO.unit) // release
      }

    def inputOutputStream(
        in: File,
        out: File): Resource[IO, (FileInputStream, FileOutputStream)] =
      for {
        inStream <- inputStream(in)
        outStream <- outputStream(out)
      } yield (inStream, outStream)

  }

  implicit val TransferIO = new Transfer[IO] {
    def copy2(source: File, dest: File): IO[Long] =
      FileContentTransferEx.inputOutputStream(source, dest).use {
        case (in, out) =>
          transfer(in, out)
      }

    def copy(source: File, dest: File): IO[Long] = {
      val inIO: IO[InputStream] = IO(new FileInputStream(source))
      val outIO: IO[OutputStream] = IO(new FileOutputStream(dest))

      (inIO, outIO) // Stage 1: Getting resources
        .tupled // From (IO[InputStream], IO[OutputStream]) to IO[(InputStream, OutputStream)]
        .bracket {
        case (in, out) =>
          transfer(in, out) // Stage 2: Using resources (for copying data, in this case)
      } {
        case (in, out) => // Stage 3: Freeing resources
          (IO(in.close()), IO(out.close())).tupled // From (IO[Unit], IO[Unit]) to IO[(Unit, Unit)]
            .handleErrorWith(_ => IO.unit)
            .void
      }
    }

    def transfer(input: InputStream, output: OutputStream): IO[Long] =
      for {
        buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
        total <- FileContentTransferEx.transmit(input, output, buffer, 0L)
      } yield total
  }

  implicit val ConsoleIO = new Console[IO] {
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
      _ <- FileContentTransferEx.main(orig, dest)
    } yield ExitCode.Success
}

// Run

// navneet_gupta> sbt
// sbt:navneet_gupta> runMain com.navneetgupta.cats.effects.ExampleApp build.sbt dest.txt
