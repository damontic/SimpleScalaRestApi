package co.s4n.domain.services

import co.s4n.config.SimpleScalaRestApiConfig

object ConfigService {
    def serve() : String = {
        f"""Vault Endpoint:\t${SimpleScalaRestApiConfig().vaultEndpoint}\n
        Vault SecretStore:\t${SimpleScalaRestApiConfig().vaultSecretStore}\n
        Vault DbEndpoint:\t${SimpleScalaRestApiConfig().dbEndpoint}\n
        Vault DbPassword:\t${SimpleScalaRestApiConfig().dbPassword}\n
        """
    }

    def reload() : String = {
        SimpleScalaRestApiConfig.reload
        "OK"
    }
}