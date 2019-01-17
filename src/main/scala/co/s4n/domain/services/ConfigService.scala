package co.s4n.domain.services

import co.s4n.config.SimpleScalaRestApiConfig

object ConfigService {
    def serve() : String = {
        s"${SimpleScalaRestApiConfig().endpoint}"
    }

    def reload() : String = {
        SimpleScalaRestApiConfig.reload
        "OK"
    }
}