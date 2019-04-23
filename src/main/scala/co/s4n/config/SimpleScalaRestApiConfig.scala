package co.s4n.config;

object SimpleScalaRestApiConfig {
	def apply(
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
					databaseDriver, databaseHost, databasePort,
					databaseName, databaseUser, databasePassword,
					databaseSslEnabled,
					serverIp, serverPort
				)

}

class SimpleScalaRestApiConfig (
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
		f"""databaseDriver: $databaseDriver
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
