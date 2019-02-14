package co.s4n.server

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

import akka.event.Logging
import akka.event.LogSource

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.settings.RoutingSettings

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import com.typesafe.config.ConfigFactory

import com.bettercloud.vault.{Vault, VaultConfig}

import co.s4n.routes.{ PrometheusRoutes, GameRoutes, RootRoutes, ConfigRoutes}
import co.s4n.config.SimpleScalaRestApiConfig

object WebServer {

    def apply() : WebServer = new WebServer()

    def loadConfiguration() : SimpleScalaRestApiConfig = {
        val DatabaseDriver      = "database_driver"
        val DatabaseHost        = "database_host"
        val DatabasePort        = "database_port"
        val DatabaseName        = "database_name"
        val DatabaseUser        = "database_user"
        val DatabasePassword    = "database_password"
        val DatabaseSslEnabled  = "database_ssl_enabled"
        val ServerPort  = "server_port"
        val ServerIp    = "server_ip"

        val conf = ConfigFactory.load()

        val vaultEndpoint : String      = sys.env.get("SSRA_VAULT_ENDPOINT").getOrElse(conf.getString("ssra.vault.endpoint"))
        val vaultSecretStore : String   = sys.env.get("SSRA_VAULT_SECRET_STORE").getOrElse(conf.getString("ssra.vault.secret_store"))
        val vaultToken : String         = sys.env.get("SSRA_VAULT_TOKEN").getOrElse(conf.getString("ssra.vault.token"))

        /* TODO: better way to get missing configurations using only one Vault connection
        val vaultConfig         = new VaultConfig().address(vaultEndpoint).token(vaultToken).build()
        val vaultClient         = new Vault(vaultConfig)
        val configurations      = vaultClient.logical().read(vaultSecretStore).getData()
        */

        val databaseDriver      = sys.env.get("SSRA_DATABASE_DRIVER").getOrElse(Some(conf.getString("ssra.database.driver"))) match {
            case Some(s : String) => s
            case None => getFromVault(DatabaseDriver) // configurations.get(DatabaseDriver)
        }
        val databaseHost        = sys.env.get("SSRA_DATABASE_HOST").getOrElse(Some(conf.getString("ssra.database.host"))) match {
            case Some(s : String) => s
            case None => getFromVault(DatabaseHost) // configurations.get(DatabaseHost)
        }
        val databasePort        = sys.env.get("SSRA_DATABASE_PORT").map(_.toInt).getOrElse(Some(conf.getInt("ssra.database.port"))) match {
            case Some(s : Int) => s
            case None => getFromVault(DatabasePort).toInt // configurations.get(DatabasePort).toInt
        }
        val databaseName        = sys.env.get("SSRA_DATABASE_NAME").getOrElse(Some(conf.getString("ssra.database.name"))) match {
            case Some(s : String) => s
            case None => getFromVault(DatabaseName) // configurations.get(DatabaseName)
        }
        val databaseUser        = sys.env.get("SSRA_DATABASE_USER").getOrElse(Some(conf.getString("ssra.database.user"))) match {
            case Some(s : String) => s
            case None => getFromVault(DatabaseUser) // configurations.get(DatabaseUser)
        }
        val databasePassword    = sys.env.get("SSRA_DATABASE_PASSWORD").getOrElse(Some(conf.getString("ssra.database.password"))) match {
            case Some(s : String) => s
            case None => getFromVault(DatabasePassword) // configurations.get(DatabasePassword)
        }
        val databaseSslEnabled  = sys.env.get("SSRA_DATABASE_SSL_ENABLED").map(_.toBoolean).getOrElse(Some(conf.getBoolean("ssra.database.ssl_enabled"))) match {
            case Some(s : Boolean) => s
            case None => getFromVault(DatabaseSslEnabled).toBoolean //configurations.get(DatabaseSslEnabled).toBoolean
        }
        val serverIp    = sys.env.get("SSRA_SERVER_IP").getOrElse(Some(conf.getString("ssra.server.ip"))) match {
            case Some(s : String) => s
            case None => getFromVault(ServerIp) //configurations.get(ServerIp)
        }
        val serverPort : Int    = sys.env.get("SSRA_SERVER_PORT").map(_.toInt).getOrElse(Try(conf.getInt("ssra.server.port"))) match {
            case Success(s : Int) => s
            case Failure(e) => getFromVault(ServerPort).toInt // configurations.get(ServerPort).toInt
        }

        SimpleScalaRestApiConfig(
            vaultEndpoint, vaultSecretStore, vaultToken,
            databaseDriver, databaseHost, databasePort,
            databaseName, databaseUser, databasePassword,
            databaseSslEnabled,
            serverIp, serverPort
        )
    }

    def getFromVault(key: String) : String = {
        val conf = ConfigFactory.load()
        val vaultEndpoint : String      = sys.env.get("SSRA_VAULT_ENDPOINT").getOrElse(conf.getString("ssra.vault.endpoint"))
        val vaultSecretStore : String   = sys.env.get("SSRA_VAULT_SECRET_STORE").getOrElse(conf.getString("ssra.vault.secret_store"))
        val vaultToken : String         = sys.env.get("SSRA_VAULT_TOKEN").getOrElse(conf.getString("ssra.vault.token"))

        val vaultConfig         = new VaultConfig().address(vaultEndpoint).token(vaultToken).build()
        val vaultClient         = new Vault(vaultConfig)
        val configurations      = vaultClient.logical().read(vaultSecretStore).getData()
        configurations.get(key)
    }

    implicit val logSource: LogSource[AnyRef] = new LogSource[AnyRef] {
        def genString(o: AnyRef): String = o.getClass.getName
        override def getClazz(o: AnyRef): Class[_] = o.getClass
    }
}

class WebServer {

    implicit val actorSystem = ActorSystem("webserver", ConfigFactory.load().getConfig("ssra.web-server-pool"))
    implicit val mat = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher

    implicit val rs : RoutingSettings = RoutingSettings(actorSystem)
    implicit val r2h = RouteResult.route2HandlerFlow _

    val log = Logging(actorSystem, this)

    var (simpleScalaRestApiConfig, routes, bindingFuture) = start()

    def restart() {
        log.debug("Server restart")
        unbind()
        ConfigFactory.invalidateCaches
        val (s, r, b) = start()
        this.simpleScalaRestApiConfig = s
        this.routes = r
        this.bindingFuture = b
    }

    def unbind() = {
        log.debug("Server unbind")
        bindingFuture.flatMap(_.unbind())
    }

    def shutdown() = {
        log.debug("Server shutdown")
        bindingFuture
                .flatMap(_.unbind())                       // trigger unbinding from the port
                .onComplete(_ => actorSystem.terminate())  // and shutdown when done
    }

    def start() : Tuple3[SimpleScalaRestApiConfig, Route, Future[ServerBinding]] = {
        log.debug("Server started")
        var simpleScalaRestApiConfig = WebServer.loadConfiguration()
        var routes =
            ConfigRoutes.routes(simpleScalaRestApiConfig, this) ~
            PrometheusRoutes.routes ~
            GameRoutes.routes(simpleScalaRestApiConfig) ~
            RootRoutes.routes
        var binding = Http().bindAndHandle(routes, simpleScalaRestApiConfig.serverIp, simpleScalaRestApiConfig.serverPort)
        (simpleScalaRestApiConfig, routes, binding)
    }

}
