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
import com.typesafe.config.Config

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

        val databaseDriver = getConfigurationString("SSRA_DATABASE_DRIVER", "ssra.database.driver", DatabaseDriver, conf)
        val databaseHost = getConfigurationString("SSRA_DATABASE_HOST", "ssra.database.host", DatabaseHost, conf)
        val databasePort = getConfigurationInt("SSRA_DATABASE_PORT", "ssra.database.port", DatabasePort, conf)
        val databaseName = getConfigurationString("SSRA_DATABASE_NAME", "ssra.database.name", DatabaseName, conf)
        val databaseUser = getConfigurationString("SSRA_DATABASE_USER", "ssra.database.user", DatabaseUser, conf)
        val databasePassword = getConfigurationString("SSRA_DATABASE_PASSWORD", "ssra.database.password", DatabasePassword, conf)
        val databaseSslEnabled = getConfigurationBoolean("SSRA_DATABASE_SSL_ENABLED", "ssra.database.ssl_enabled", DatabaseSslEnabled, conf)
        val serverIp = getConfigurationString("SSRA_SERVER_IP", "ssra.server.ip", ServerIp, conf)
        val serverPort = getConfigurationInt("SSRA_SERVER_PORT", "ssra.server.port", ServerPort, conf)

        SimpleScalaRestApiConfig(
            vaultEndpoint, vaultSecretStore, vaultToken,
            databaseDriver, databaseHost, databasePort,
            databaseName, databaseUser, databasePassword,
            databaseSslEnabled,
            serverIp, serverPort
        )
    }

    def getConfigurationString(envVar : String, configName: String, consulName: String, conf: Config) : String = {
        sys.env.get(envVar) match {
            case Some(s) => s
            case None => Try(conf.getString(configName)) match {
                case Success(s) => s
                case Failure(e) => getFromVault(consulName)
            }
        }
    }

    def getConfigurationInt(envVar : String, configName: String, consulName: String, conf: Config) : Int = {
        sys.env.get(envVar) match {
            case Some(s) => s.toInt
            case None => Try(conf.getInt(configName)) match {
                case Success(s) => s
                case Failure(e) => getFromVault(consulName).toInt
            }
        }
    }

    def getConfigurationBoolean(envVar : String, configName: String, consulName: String, conf: Config) : Boolean = {
        sys.env.get(envVar) match {
            case Some(s) => s.toBoolean
            case None => Try(conf.getBoolean(configName)) match {
                case Success(s) => s
                case Failure(e) => getFromVault(consulName).toBoolean
            }
        }
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
