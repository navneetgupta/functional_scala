package com.navneetgupta.freestyle

import freestyle.free.free
import freestyle.tagless.tagless


object ModuleEx {

  import freestyle.free.free
  import freestyle.tagless.tagless
  import freestyle.free.implicits._

  @tagless(true) trait Database {
    def get(id: Long): FS[Long]
  }
  @tagless(true) trait Cache {
    def get(id : Long) : FS[Option[Long]]
  }
  @free trait Presenter {
    def show(id: Long) : FS[Long]
  }
  @free trait IdValidation {
    def validate(id: Option[Long]) : FS[Long]
  }
}

object Modules {
  import freestyle.free._
  import ModuleEx._

  @module trait Persistence {
    val database: Database.StackSafe
    val cache: Cache.StackSafe
  }

  @module trait Display {
    val presenter: Presenter
    val idValidation : IdValidation
  }

  @module trait MApp {
    val persistence: Persistence
    val display: Display
  }
}

object ModuleProgram {
  import Modules._
  import freestyle.free._
  def program[F[_]](implicit app: MApp[F]): FreeS[F, Long]  = {
    import app.display._, app.persistence._
    for {
    cachedToken <- cache.get(1)
    id          <- idValidation.validate(cachedToken)
    value       <- database.get(id)
    view <- presenter.show(value)
  } yield view

  }
}
object ModuleApp extends App {
  implicit val

}
