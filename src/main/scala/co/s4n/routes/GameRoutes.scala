package co.s4n.routes

import com.lonelyplanet.prometheus.PrometheusResponseTimeRecorder
import com.lonelyplanet.prometheus.directives.ResponseTimeRecordingDirectives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }

import co.s4n.domain.services.GameService
import co.s4n.config.SimpleScalaRestApiConfig

object GameRoutes {
    private val responseTimeDirectives = ResponseTimeRecordingDirectives(PrometheusResponseTimeRecorder.Default)
    import responseTimeDirectives._
    def routes(config : SimpleScalaRestApiConfig) = {
        get {
            path("games") {
                recordResponseTime("/games") {
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, GameService.games(config)))
                }
            }
        }
    }
}
