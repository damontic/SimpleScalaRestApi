package co.s4n.domain.services

import co.s4n.domain.vo.Game
import co.s4n.domain.repositories.GameRepository
import co.s4n.config.SimpleScalaRestApiConfig

object GameService {
    def gamesAsHeaders(config: SimpleScalaRestApiConfig) : String = {
        val games = GameRepository.games(config).map(_.name)
        val gamesString = games.mkString("</h1><h1>")
        s"<h1>$gamesString</h1>"
    }

    def gameAsHeader(config: SimpleScalaRestApiConfig, id: Int) : String = {
        val game = GameRepository.gameById(config, id).map(_.name)
        s"<h1>$game</h1>"
    }
}