package co.s4n.server

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route

import co.s4n.routes.{ PrometheusRoutes, HelloRoutes, RootRoutes}

object WebServer extends HttpApp {
  override def routes: Route =
    PrometheusRoutes.routes ~
    HelloRoutes.routes ~
    RootRoutes.routes
}
