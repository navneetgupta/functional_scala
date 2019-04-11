package com.typelevel.blogs.algebraic_api_design

import cats.Show

trait GameLogger[F[_]] {
  def log[T: Show](msg: T): F[Unit]
}

object GameLogger {
  implicit def instance[F[_]]()(implicit G: GameLogger[F]) = G
}


