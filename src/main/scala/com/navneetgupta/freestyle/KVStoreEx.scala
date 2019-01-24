package com.navneetgupta.freestyle

import com.navneetgupta.freestyle.KVStoreEx.Backend
import freestyle.free._
import freestyle.free.implicits._
import scalaz.Alpha.K

object KVStoreEx {
  @free trait KVStore {
    def get[A](key: String): FS[Option[A]]
    def put[A](key : String, value: A) : FS[Unit]
    def delete[A](key: String) : FS[Unit]
    def update[A](key: String, f: A => A): FS.seq[Unit] =
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

object KVStoreInterpreters {
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
}

object KVStoreApp extends App {
  def program[F[_]](implicit backend: Backend): Frees[F, Unit] = {
    import backend.kvstore._, import backend.log._
    for {
      
    }
  }
}