package com.typelevel.blogs.algebraic_api_design

import cats.Functor

final case class GameState[M, B, S](
                                   playedMoves: List[M],
                                   score: S,
                                   position: B
                                   )

trait Game[F[_], M, B, S] {
  type GS = GameState[M, B, S]

  def applyMove(gameState: GS, move: M): GS
  def legalMoves(gameState: GS): List[M]
  def simulation(gameState: GS): F[GS]
}

object Game{
  object laws {

    import cats.syntax.functor._

    def simulationIsTerminal[F[_]: Functor, M, B, S]
      (gameState: GameState[M , B, S])(implicit ev: Game[F, M, B, S]): Unit = {
      ev.simulation(gameState).map(ev.legalMoves).map(a => a.isEmpty)
    }

    def legalMovesLeadsToNewState[F[_], M, B, S]
      (gameState: GameState[M, B, S], move: M)(implicit ev : Game[F, M, B, S]) = {
      val legalMoves = ev.legalMoves(gameState)
      val newGameState = ev.applyMove(gameState, move)
      !legalMoves.contains(move) ||
        (newGameState.position != gameState.position && newGameState.playedMoves.length == gameState.playedMoves.length + 1)
    }
  }
}