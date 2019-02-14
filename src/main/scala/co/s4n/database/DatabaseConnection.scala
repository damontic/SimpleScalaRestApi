package co.s4n.database

import co.s4n.config.SimpleScalaRestApiConfig

import java.sql.DriverManager
import java.sql.Connection
import java.util.Properties

object DatabaseConnection {
    def databaseConnection(config: SimpleScalaRestApiConfig) : Connection = {
        Class.forName(config.databaseDriver)
        val dbUrl = s"jdbc:postgresql://${config.databaseHost}:${config.databasePort}/${config.databaseName}"
        var properties = new Properties()
        properties.setProperty("user", config.databaseUser)
        properties.setProperty("password", config.databasePassword)
        properties.setProperty("ssl", config.databaseSslEnabled.toString)
        DriverManager.getConnection(dbUrl, properties)
    }
}