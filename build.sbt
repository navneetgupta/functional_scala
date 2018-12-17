

name := "scalaz7_examples"

organization := "com.navneetgupta"

scalaVersion := "2.12.6"

version      := "0.1.0"

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ypartial-unification",
  "-language:higherKinds",
  "-language:implicitConversions"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies ++= {
	val akkaVersion = "2.5.13"
	val catVersion = "1.2.0"
	Seq(
		"com.typesafe.akka" %% "akka-actor" % akkaVersion,
		"org.scalaz" %% "scalaz-core" % "7.2.26",
		"org.scalaz" %% "scalaz-concurrent" % "7.2.26",
    "org.scalaz" %% "scalaz-iteratee" % "7.2.26",
		"org.typelevel" %% "cats-core" % catVersion,
    "org.typelevel" %% "cats-macros" % catVersion,
    "org.typelevel" %% "cats-kernel" % catVersion,
    "org.typelevel" %% "cats-laws" % catVersion,
    "org.typelevel" %% "cats-free" % catVersion,
    "org.typelevel" %% "cats-free" % catVersion,
    "org.typelevel" %% "cats-effect" % "1.0.0-RC2",
    "org.typelevel" %% "kittens" % "1.1.1",
    "com.github.mpilquist" %% "simulacrum" % "0.14.0",
    "org.scalatest" %% "scalatest" % "3.0.5",
    "com.chuusai" %% "shapeless" % "2.3.3",
    "org.scalaz" %% "scalaz-zio" % "0.5.1"
	)
}