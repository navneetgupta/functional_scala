import Dependencies._

name := "scalaz7_examples"

organization := "com.navneetgupta"

scalaVersion := "2.12.6"

version      := "0.1.0"

libraryDependencies ++= {
	val akkaVersion = "2.5.13"
	Seq(
		"com.typesafe.akka" %% "akka-actor" % akkaVersion,
		"org.scalaz" %% "scalaz-core" % "7.2.26"
	)
}