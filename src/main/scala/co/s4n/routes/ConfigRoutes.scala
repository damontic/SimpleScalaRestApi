package co.s4n.routes

import com.lonelyplanet.prometheus.PrometheusResponseTimeRecorder
import com.lonelyplanet.prometheus.directives.ResponseTimeRecordingDirectives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }

import co.s4n.domain.services.ConfigService
import co.s4n.config.SimpleScalaRestApiConfig
import co.s4n.server.WebServer

object ConfigRoutes {
    private val responseTimeDirectives = ResponseTimeRecordingDirectives(PrometheusResponseTimeRecorder.Default)
    import responseTimeDirectives._

    def routes(conf : SimpleScalaRestApiConfig, server : WebServer) = {
        get {
            path("config" / "reload") {
                recordResponseTime("/config/reload") {
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ConfigService.reload(server)))
                }
            }
        } ~
        get {
            path("config") {
                recordResponseTime("/config") {
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ConfigService.serve(conf)))
                }
            }
        }
    }
}
