package com.navneetgupta.cats.effects

import scala.collection.SortedSet
import cats.effect._
import java.io.{BufferedReader, File, FileReader}
import java.util.UUID
import scala.concurrent.duration._
import java.util.stream.Collectors
import cats.implicits._

object TaglessAnotherEx {
  import Common._

  case class Band(value: String) extends AnyVal

  implicit val bandOrdering: Ordering[Band] = (x: Band, y: Band) => x.value.compareTo(y.value)

  implicit val ConsoleIO = new Common.Console[IO] {
    def putStrLn(line: String): IO[Unit] = IO(println(line))

    def readLn(): IO[String] = IO(scala.io.StdIn.readLine)
  }

  val getBandsFromFile: IO[List[Band]] = IO {
    val file = new File(this.getClass.getClassLoader.getResource("bands.txt").getFile)
    new BufferedReader(new FileReader(file))
  }.flatMap {br =>
    import scala.collection.JavaConverters._
    val bands = br.lines.collect(Collectors.toList()).asScala.toList.map(Band)
    IO.pure(bands) <* IO(br.close())
  }

  def generateId: IO[UUID]= IO(UUID.randomUUID())

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
}

object TaglessAnotherExApp extends App {
  TaglessAnotherEx.generateChart.unsafeRunSync()
}
