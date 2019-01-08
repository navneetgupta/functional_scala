package com.navneetgupta.cats.effects

import java.io.{BufferedReader, File, FileReader}
import java.util.UUID
import java.util.stream.Collectors

import cats.effect._
import cats.syntax.all._

import scala.collection.SortedSet

object TaglessAnotherBadEx extends IOApp {

  import Common._

  case class Band(value: String) extends AnyVal

  implicit val bandOrdering: Ordering[Band] = (x: Band, y: Band) => x.value.compareTo(y.value)

  implicit val ConsoleIO = new Common.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }

  val getBandsFromFile: IO[List[Band]] = IO {
    val file = new File("bands.txt") //(this.getClass.getClassLoader.getResource("bands.txt").getFile)
    new BufferedReader(new FileReader(file))
  }.flatMap {br =>
    import scala.collection.JavaConverters._
    val bands = br.lines.collect(Collectors.toList()).asScala.toList.map(Band)
    IO.pure(bands) <* IO(br.close())
  }


  def generateId: IO[UUID]= IO(UUID.randomUUID())

  import scala.concurrent.duration._

  def longProcess1(bands: List[Band]): IO[Unit] =
    putStrLn("Starting process 1") *> IO.sleep(3.seconds) *> putStrLn("Process 1 DONE")

  def longProcess2(bands: List[Band]): IO[Unit] =
    putStrLn("Starting process 2") *> IO.sleep(2.seconds) *> putStrLn("Process 2 DONE")

  def publishRadioChart(id: UUID, bands: SortedSet[Band]): IO[Unit] =
    putStrLn(s"Radio Chart for $id: ${bands.map(_.value).mkString(", ")}")

  def publishTvChart(id: UUID, bands: SortedSet[Band]): IO[Unit] =
    putStrLn(s"TV Chart for $id: ${bands.take(5).map(_.value).mkString(", ")}")

  val generateChart: IO[Unit] =
    for {
      b <- getBandsFromFile
      _ <- IO.race(longProcess1(b),longProcess2(b)) // who ever completes first of two long running process
      id <- generateId
      _ <- publishRadioChart(id, SortedSet( b: _*))
      _ <- publishTvChart(id, SortedSet(b: _*))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- generateChart    //Timer[IO] default instace provided by IOApp
    } yield ExitCode.Success
}


object TaglessAnotherImprovedSoln {

  case class Band(value: String) extends AnyVal
  object Algebras {
    trait DataSource[F[_]] {
      def bands: F[List[Band]]
    }
    trait IdGen[F[_]] {
      def generateId: F[UUID]
    }
    trait InternalProcess[F[_]] {
      def process(bands: SortedSet[Band]): F[Unit]
    }
    trait RadioChart[F[_]] {
      def publish(id: UUID, bands: SortedSet[Band]): F[Unit]
    }
    trait TvChart[F[_]] {
      def publish(id: UUID,bands: SortedSet[Band]): F[Unit]
    }
    def bands[F[_]: DataSource] = implicitly[DataSource[F]].bands
    def generateId[F[_]: IdGen] = implicitly[IdGen[F]].generateId
    def process[F[_]: InternalProcess](bands: SortedSet[Band]) = implicitly[InternalProcess[F]].process(bands)
    def publishRadio[F[_]: RadioChart](id: UUID, bands: SortedSet[Band])= implicitly[RadioChart[F]].publish(id, bands)
    def publishTv[F[_]: TvChart](id: UUID, bands: SortedSet[Band])= implicitly[TvChart[F]].publish(id, bands)
  }

  object AlgebraInterpretations {
    import Algebras._
    class MemRad
  }
  import Algebras._
  import cats.Monad

  def generateCharts[F[_]: Monad : DataSource : IdGen : InternalProcess: RadioChart: TvChart]: F[Unit] =
    for {
      b <- bands.map(xs => SortedSet(xs: _*))
      _ <- process(b)
      id <- generateId
      _ <- publishRadio(id, b)
      _ <- publishTv(id, b)
    } yield ()
}

