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

import co.s4n.routes.{ PrometheusRoutes, GameRoutes, RootRoutes, ConfigRoutes}
import co.s4n.config.SimpleScalaRestApiConfig

object WebServer {

    def apply() : WebServer = new WebServer()

    def loadConfiguration() : SimpleScalaRestApiConfig = {
        val conf = ConfigFactory.load()

        val databaseDriver = conf.getString("ssra.database.driver")
        val databaseHost = conf.getString("ssra.database.host")
        val databasePort = conf.getInt("ssra.database.port")
        val databaseName = conf.getString("ssra.database.name")
        val databaseUser = conf.getString("ssra.database.user")
        val databasePassword = conf.getString("ssra.database.password")
        val databaseSslEnabled = conf.getBoolean("ssra.database.ssl_enabled")
        val serverIp = conf.getString("ssra.server.ip")
        val serverPort = conf.getInt("ssra.server.port")

        SimpleScalaRestApiConfig(
            databaseDriver, databaseHost, databasePort,
            databaseName, databaseUser, databasePassword,
            databaseSslEnabled,
            serverIp, serverPort
        )
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
