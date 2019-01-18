package co.s4n.environment

sealed trait Environment

object Production extends Environment
object Staging extends Environment
object Development extends Environment
object Local extends Environment