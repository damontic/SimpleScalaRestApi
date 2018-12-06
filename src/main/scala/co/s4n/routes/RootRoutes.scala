package co.s4n.routes

import com.lonelyplanet.prometheus.PrometheusResponseTimeRecorder
import com.lonelyplanet.prometheus.directives.ResponseTimeRecordingDirectives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }

object RootRoutes {
    private val responseTimeDirectives = ResponseTimeRecordingDirectives(PrometheusResponseTimeRecorder.Default)
    import responseTimeDirectives._
    val routes = {
        get {
            path("") {
                recordResponseTime("/") {
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to the Root route</h1>"))
                }
            }
        }
    }
}