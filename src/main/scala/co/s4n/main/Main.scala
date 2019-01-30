package co.s4n.main

import co.s4n.config.SimpleScalaRestApiConfig
import co.s4n.server.WebServer

object Main {

	def main(args: Array[String]) = {
		val vaultEndpoint = sys.env.get("VAULT_ENDPOINT")
		val vaultSecretStore = sys.env.get("VAULT_SECRET_STORE")
		val vaultToken = sys.env.get("VAULT_TOKEN")
		(vaultEndpoint, vaultSecretStore, vaultToken) match {
			case (Some(ve), Some(vss), Some(vt)) => {
				SimpleScalaRestApiConfig.init(ve, vss, vt)
				WebServer.startServer("0.0.0.0", 8080)
			}
			case default => System.err.println("Make sure that the env vars VAULT_ENDPOINT and VAULT_SECRET_STORE are defined.")
		}
	}

}