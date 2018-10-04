package com.navneetguota.scala

case class Name(firstName: String, lastName: String)
case class Age(age: Int)
case class Person(name: Name, age: Age)
case class Config(name: String, age: Int)

object ImplicitsUse extends App {
  def readName(config: Config): Name = {
    val parts = config.name.split(" ")
    require(parts.size >= 2)
    Name(parts(0), parts.tail.mkString(" "))
  }

  def readAge(config: Config): Age = {
    val age = config.age
    require(1 <= age && age <= 50)
    Age(age)
  }

  def readPerson(config: Config): Option[Person] = {
    try Some(Person(readName(config), readAge(config)))
    catch { case ex: IllegalArgumentException => None }
  }

  println(readPerson(Config("John Doe", 20)))
  println(readPerson(Config("Incognito", 99)))
}
// Above lots of params need to be passed arround Suppose if we have to parse multiple person attribute color, sex,education,location,address,experience etc

object ImplicitsUseWithImplicts extends App {
  def readName(implicit config: Config): Name = {
    val parts = config.name.split(" ")
    require(parts.size >= 2)
    Name(parts(0), parts.tail.mkString(" "))
  }

  def readAge(implicit config: Config): Age = {
    val age = config.age
    require(1 <= age && age <= 50)
    Age(age)
  }

  def readPerson(implicit config: Config): Option[Person] = {
    try Some(Person(readName, readAge))
    catch { case ex: IllegalArgumentException => None }
  }

  implicit val config = Config("John Doe", 20)
  println(readPerson)
  //implicit val config2 = Config("John Doe", 20)
  println(readPerson)
}

// Even above we have to repeat implicit config: Config multiple times and more if our Person has more attributes.

object ImplicitsUseWithImplicts2 extends App {

  def readName = {
    implicit config: Config => {
      val parts = config.name.split(" ")
      require(parts.size >= 2)
      Name(parts(0), parts.tail.mkString(" "))
    }
  }

  def readAge = {
    implicit config: Config => {
      val age = config.age
      require(1 <= age && age <= 50)
      Age(age)
    }
  }

  def readPerson = {
    implicit config: Config => {
      try Some(Person(readName, readAge))
      catch { case ex: IllegalArgumentException => None }
    }
  }

  implicit val config = Config("John Doe", 20)
  println(readPerson)
  //implicit val config2 = Config("John Doe", 20)
  println(readPerson)
}

object Configs {
  type Configured[T] = implicit Config => T
  def config: Configured[Config] = implicitly[Config]
}
