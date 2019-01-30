package co.s4n.config;

import scala.util.{Try, Success, Failure}

import co.s4n.environment.{Production , Staging, Development, Local}
import com.bettercloud.vault.{Vault, VaultConfig}

object SimpleScalaRestApiConfig {

	val DatabaseEndpoint = "database_endpoint"
	val DatabasePassword = "database_password"

	var config : Option[SimpleScalaRestApiConfig] = None

	def apply(
				vaultEndpoint: String,
				vaultSecretStore: String,
				dbEndpoint: String,
				dbPassword: String
			) : SimpleScalaRestApiConfig =
				new SimpleScalaRestApiConfig(
												vaultEndpoint,
												vaultSecretStore,
												dbEndpoint,
												dbPassword
					)

	def apply() : SimpleScalaRestApiConfig = config match {
		case Some(c) => c
		case None => throw new Exception("You must init the configuration first.")
	}

	def init(vaultEndpoint: String, vaultSecretStore: String) : Unit = {
		println(s"[info] init params: vaultEndpoint = $vaultEndpoint / vaultSecretStore = $vaultSecretStore")
		val vaultConfig = new VaultConfig().address(vaultEndpoint).build()
		val vaultClient = new Vault(vaultConfig)
		val configurations = vaultClient.logical().read(vaultSecretStore).getData()
		val dbEndpoint = configurations.get(DatabaseEndpoint)
		val dbPassword = configurations.get(DatabasePassword)

		config = Some(SimpleScalaRestApiConfig(
			vaultEndpoint,
			vaultSecretStore,
			dbEndpoint,
			dbPassword
		))
}

	def reload() : Unit = {
		init(config.get.vaultEndpoint, config.get.vaultSecretStore)
	}

}

class SimpleScalaRestApiConfig (
	val vaultEndpoint: String,
	val vaultSecretStore: String,
	val dbEndpoint: String,
	val dbPassword: String
)
