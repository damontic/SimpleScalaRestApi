package co.s4n.domain.repositories

import co.s4n.domain.vo.Game
import co.s4n.database.DatabaseConnection
import co.s4n.config.SimpleScalaRestApiConfig

import java.sql.ResultSet

object GameRepository {

    val GAME_ID = "id"
    val GAME_NAME = "name"
    val GAME_SELECT = "SELECT id, name FROM games"

    def games(config: SimpleScalaRestApiConfig) : List[Game] = {
        val connection = DatabaseConnection.databaseConnection(config)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(GAME_SELECT)

        def helper(list: List[Game], resultSet : ResultSet) : List[Game] = {
            if (resultSet.next()) helper(list :+ Game(resultSet.getInt(GAME_ID), resultSet.getString(GAME_NAME)), resultSet)
            else list
        }
        helper(List(), resultSet)
    }

    def gameById(config: SimpleScalaRestApiConfig, id: Int) : Option[Game] = {
        val connection = DatabaseConnection.databaseConnection(config)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(s"$GAME_SELECT WHERE id = $id")

        if (resultSet.next()) Some(Game(resultSet.getInt(GAME_ID), resultSet.getString(GAME_NAME)))
        else None
    }
}