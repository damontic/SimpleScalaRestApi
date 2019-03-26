package co.s4n.domain.services

import co.s4n.config.SimpleScalaRestApiConfig
import co.s4n.server.WebServer

object ConfigService {
    def serve(conf: SimpleScalaRestApiConfig) : String = {
        s"""${conf.toString}"""
    }

    def reload(server: WebServer) : String = {
        server.restart
        "OK"
    }
}