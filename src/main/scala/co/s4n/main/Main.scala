package co.s4n.main

import co.s4n.config.SimpleScalaRestApiConfig
import co.s4n.server.WebServer

object Main {

	def startSimpleRestApi(environment : String) {
		WebServer.startServer("0.0.0.0", 8080)
	}

	def main(args: Array[String]) = {
		sys.env.get("ENVIRONMENT") match {
			case Some(e) =>
				SimpleScalaRestApiConfig.init()
				startSimpleRestApi(e)
			case None	 =>
				System.err.println("ENVIRONMENT env var not found.")
		}
	}
}