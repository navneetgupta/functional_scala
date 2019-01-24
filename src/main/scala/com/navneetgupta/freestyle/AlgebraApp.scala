package com.navneetgupta.freestyle

object AlgebraEx {

//  case class User(name: String, email: String, id: Long)
//
//  @free trait Users {
//    def get(id: Long): FS[Option[User]]
//    def save(user: User): FS[User]
//    def getAll(filter: String): FS[List[User]]
//  }
//
//  @module trait UserApplication {
//    val users: Users
//
//    def program(user: User): FS.Seq[User] =
//      for {
//        _         <- users.save(user)
//        user      <- users.get(user.id)
//      } yield user
//  }
}

object AlgebraExApp extends App {
//  import AlgebraEx._
//  import cats.effect.IO
//  import cats.implicits._
//
//  var usersList = List(User("Navneet Gupta", "nkumar@adp.com", 1), User("Gupta", "gupta@adp.com", 2), User("Navneet", "navneet@gmail.com",3))
//
//  implicit val UserHandler = new Users.Handler[IO] {
//    override protected def get(id: Long): IO[Option[User]] = IO{usersList.filter(_.id == id).get(0)}
//
//    override protected def getAll(filter: String):IO[List[User]] = IO {usersList}
//
//    override protected def save(user: User): IO[User] = IO{
//      usersList.filter(_.id == user.id) match {
//        case user::users => user
//        case Nil => usersList = user::usersList; user
//      }}
//  }
}