package com.navneetgupta.freestyle

import cats.implicits._
import freestyle.free._
import freestyle.free.implicits._

object KVStoreEx {

  @free trait KVStore {
    def get[A](key: String): FS[Option[A]]

    def put[A](key: String, value: A): FS[Unit]

    def delete[A](key: String): FS[Unit]

    def update[A](key: String, f: A => A): FS.Seq[Unit] =
      get[A](key).freeS flatMap {
        case Some(a) => put[A](key, f(a)).freeS
        case None => ().pure[FS.Seq]
      }
  }

  @free trait Log {
    def info(msg: String): FS[Unit]

    def warn(msg: String): FS[Unit]
  }

  @module trait Backend {
    val kvstore: KVStore
    val log: Log
  }

}

object KVStoreApp extends App {
  import KVStoreEx._
  import cats.data.State

  type KVStoreState[A] = State[Map[String, Any], A]
  implicit val kvStoreHandler: KVStore.Handler[KVStoreState] = new KVStore.Handler[KVStoreState] {
    def get[A](key: String): KVStoreState[Option[A]] =
      State.inspect(_.get(key).map(_.asInstanceOf[A]))

    def put[A](key: String, value: A): KVStoreState[Unit] =
      State.modify(_.updated(key, value))

    def delete[A](key: String): KVStoreState[Unit] = State.modify(_ - key)
  }

  implicit val logHandler = new Log.Handler[KVStoreState] {
    override def info(msg: String): KVStoreState[Unit] = println(s"INFO: $msg").pure[KVStoreState]

    override def warn(msg: String): KVStoreState[Unit] = println(s"WARN: $msg").pure[KVStoreState]
  }

  def program[F[_]](implicit backend: Backend[F]): FreeS[F,Option[Int]] = {
    import backend.kvstore._
    import backend.log._
    for {
      _ <- put("wild-cats", 2)
      _ <- info("Added wild-cats")
      _ <- update[Int]("wild-cats", (_ + 12))
      _ <- info("Updated wild-cats")
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
      _ <- warn("Deleted tame-cats")
    } yield n
  }

  import freestyle.free.implicits._

  println(program[Backend.Op].interpret[KVStoreState].run(Map.empty).value)
}