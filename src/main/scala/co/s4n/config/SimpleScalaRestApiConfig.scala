package co.s4n.config;

object SimpleScalaRestApiConfig {

	val DatabaseEndpoint = "database_endpoint"
	val DatabasePassword = "database_password"

	var config : Option[SimpleScalaRestApiConfig] = None

	def apply(
					vaultEndpoint: String,
					vaultSecretStore: String,
					vaultToken: String,
					databaseDriver: String,
					databaseHost: String,
					databasePort: Int,
					databaseName: String,
					databaseUser: String,
					databasePassword: String,
					databaseSslEnabled: Boolean,
					serverIp: String,
					serverPort: Int
			) : SimpleScalaRestApiConfig =
				new SimpleScalaRestApiConfig(
					vaultEndpoint, vaultSecretStore, vaultToken,
					databaseDriver, databaseHost, databasePort,
					databaseName, databaseUser, databasePassword,
					databaseSslEnabled,
					serverIp, serverPort
				)

}

class SimpleScalaRestApiConfig (
	val vaultEndpoint: String,
	val vaultSecretStore: String,
	val vaultToken: String,
	val databaseDriver: String,
	val databaseHost: String,
	val databasePort: Int,
	val databaseName: String,
	val databaseUser: String,
	val databasePassword: String,
	val databaseSslEnabled: Boolean,
	val serverIp: String,
	val serverPort: Int
) {
	override def toString() : String = {
		f"""vaultEndpoint: $vaultEndpoint
vaultSecretStore: $vaultSecretStore
vaultToken: $vaultToken
databaseDriver: $databaseDriver
databaseHost: $databaseHost
databasePort: $databasePort
databaseName: $databaseName
databaseUser: $databaseUser
databasePassword: $databasePassword
databaseSslEnabled: $databaseSslEnabled
serverIp: $serverIp
serverPort: $serverPort
"""
	}
}
