package com.navneetgupta.cats.effects

import java.io.{BufferedReader, File, FileReader}
import java.util.UUID
import java.util.stream.Collectors

import cats.effect._
import cats.syntax.all._

import scala.collection.SortedSet
import Common._

object TaglessAnotherBadEx extends IOApp {

  case class Band(value: String) extends AnyVal

  implicit val bandOrdering: Ordering[Band] = (x: Band, y: Band) => x.value.compareTo(y.value)

  implicit val ConsoleIO = new Common.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }

  val getBandsFromFile: IO[List[Band]] = IO {
    val file = new File("bands.txt") //(this.getClass.getClassLoader.getResource("bands.txt").getFile)
    new BufferedReader(new FileReader(file))
  }.flatMap { br =>
    import scala.collection.JavaConverters._
    val bands = br.lines.collect(Collectors.toList()).asScala.toList.map(Band)
    IO.pure(bands) <* IO(br.close())
  }


  def generateId: IO[UUID] = IO(UUID.randomUUID())

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
      _ <- IO.race(longProcess1(b), longProcess2(b)) // who ever completes first of two long running process
      id <- generateId
      _ <- publishRadioChart(id, SortedSet(b: _*))
      _ <- publishTvChart(id, SortedSet(b: _*))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- generateChart //Timer[IO] default instace provided by IOApp
    } yield ExitCode.Success
}


object TaglessAnotherImprovedSoln {

  object Algebras {

    case class Band(value: String) extends AnyVal

    trait DataSource[F[_]] {
      def bands: F[List[Band]]
    }

    trait IdGen[F[_]] {
      def generateId: F[UUID]
    }

    trait InternalProcess[F[_]] {
      def process1(bands: SortedSet[Band]): F[Unit]

      def process2(bands: SortedSet[Band]): F[Unit]
    }

    trait RadioChart[F[_]] {
      def publish(id: UUID, bands: SortedSet[Band]): F[Unit]
    }

    trait TvChart[F[_]] {
      def publish(id: UUID, bands: SortedSet[Band]): F[Unit]
    }

    def bands[F[_] : DataSource] = implicitly[DataSource[F]].bands

    def generateId[F[_] : IdGen] = implicitly[IdGen[F]].generateId

    def process1[F[_] : InternalProcess](bands: SortedSet[Band]) = implicitly[InternalProcess[F]].process1(bands)

    def process2[F[_] : InternalProcess](bands: SortedSet[Band]) = implicitly[InternalProcess[F]].process2(bands)

    def publishRadio[F[_] : RadioChart](id: UUID, bands: SortedSet[Band]) = implicitly[RadioChart[F]].publish(id, bands)

    def publishTv[F[_] : TvChart](id: UUID, bands: SortedSet[Band]) = implicitly[TvChart[F]].publish(id, bands)
  }

  import Algebras._
  import cats.Monad

  implicit val bandOrdering: Ordering[Band] = (x: Band, y: Band) => x.value.compareTo(y.value)

  def generateCharts[F[_] : Monad : DataSource : IdGen : InternalProcess : RadioChart : TvChart]: F[Unit] =
    for {
      b <- bands.map(xs => SortedSet(xs: _*))
      _ <- process1(b)
      id <- generateId
      _ <- publishRadio(id, b)
      _ <- publishTv(id, b)
    } yield ()
}

object TaglessAnotherImprovedSolnApp extends IOApp {

  import TaglessAnotherImprovedSoln.Algebras._

  implicit val ConsoleIO = new Common.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }
  implicit val DataSourceIO = new DataSource[IO] {
    def bands: IO[List[Band]] = IO {
      val file = new File("bands.txt") //(this.getClass.getClassLoader.getResource("bands.txt").getFile)
      new BufferedReader(new FileReader(file))
    }.flatMap { br =>
      import scala.collection.JavaConverters._
      val bands = br.lines.collect(Collectors.toList()).asScala.toList.map(Band)
      IO.pure(bands) <* IO(br.close())
    }
  }
  implicit val IdGenIO = new IdGen[IO] {
    def generateId: IO[UUID] = IO(UUID.randomUUID())
  }

  import scala.concurrent.duration._

  implicit val InternalProcessIO = new InternalProcess[IO] {
    def process1(bands: SortedSet[Band]): IO[Unit] = putStrLn("Starting process 1") *> IO.sleep(2.seconds) *> putStrLn("Process 1 DONE")

    def process2(bands: SortedSet[Band]): IO[Unit] = putStrLn("Starting process 2") *> IO.sleep(3.seconds) *> putStrLn("Process 2 DONE")
  }

  implicit val RadioChartIO = new RadioChart[IO] {
    def publish(id: UUID, bands: SortedSet[Band]): IO[Unit] =
      putStrLn(s"Radio Chart for $id: ${bands.map(_.value).mkString(", ")}")
  }

  implicit val TvChartIO = new TvChart[IO] {
    def publish(id: UUID, bands: SortedSet[Band]): IO[Unit] =
      putStrLn(s"TV Chart for $id: ${bands.take(5).map(_.value).mkString(", ")}")
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- TaglessAnotherImprovedSoln.generateCharts[IO] //Timer[IO] default instace provided by IOApp
    } yield ExitCode.Success
}
