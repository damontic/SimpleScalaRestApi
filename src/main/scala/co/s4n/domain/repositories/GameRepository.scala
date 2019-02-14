package co.s4n.domain.repositories

import co.s4n.domain.vo.Game
import co.s4n.database.DatabaseConnection
import co.s4n.config.SimpleScalaRestApiConfig

import java.sql.ResultSet

object GameRepository {
    def games(config: SimpleScalaRestApiConfig) : List[Game] = {
        val connection = DatabaseConnection.databaseConnection(config)
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT id, name FROM games")

        def helper(list: List[Game], resultSet : ResultSet) : List[Game] = {
            if (resultSet.next()) helper(list :+ Game(resultSet.getInt("id"), resultSet.getString("name")), resultSet)
            else list
        }
        helper(List(), resultSet)
    }
}