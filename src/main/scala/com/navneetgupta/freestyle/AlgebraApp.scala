package com.navneetgupta.freestyle

import freestyle.free._

object AlgebraEx {

  case class User(name: String, email: String, id: Long)

  @free trait Users {
    def get(id: Long): FS[User]
    def save(user: User): FS[User]
    def getAll(filter: String): FS[List[User]]
  }

  @module trait UserApplication {
    val users: Users

    import cats.implicits._

    def program(user: User): FS.Seq[User] =
      for {
        _         <- users.save(user)
        user      <- users.get(user.id)
      } yield user
  }
}

object AlgebraExApp extends App {
  
}