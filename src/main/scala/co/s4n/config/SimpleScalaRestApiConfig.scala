package co.s4n.config;

import com.ecwid.consul.v1.ConsulClient
import com.bettercloud.vault.{Vault, VaultConfig}

object SimpleScalaRestApiConfig {
	val environment : String = sys.env.get("ENVIRONMENT").get
	val consulEndpoint = environment match {
		case "Production" => "consul-helm-consul-server.default.svc.cluster.local"
		case "Staging" => "consul-helm-consul-server.default.svc.cluster.local"
		case "Development" => "consul-helm-consul-server.default.svc.cluster.local"
		case default => "localhost"
	}
	val vaultEndpoint = environment match {
		case "Production" => "https://vault-vault.default.svc.cluster.local:8200"
		case "Staging" => "http://vault-vault.default.svc.cluster.local:8200"
		case "Development" => "http://vault-vault.default.svc.cluster.local:8200"
		case default => "http://127.0.0.1:8200"
	}

	val consulClient = new ConsulClient(consulEndpoint)
	// val vaultClient = initVaultclient()
	var config : SimpleScalaRestApiConfig = init
	
	def apply(endpoint: String, password: String) : SimpleScalaRestApiConfig = new SimpleScalaRestApiConfig(endpoint, password)
	def apply() : SimpleScalaRestApiConfig = config

	def init() : SimpleScalaRestApiConfig = {
		val endpoint = consulClient.getKVValue(s"$environment/endpoint").getValue.getDecodedValue;

		// val password = vaultClient.logical().read("secret/password").getData().get("value");

		SimpleScalaRestApiConfig(endpoint, "password");
	}

	def reload() {
		config = init
	}

	def initVaultclient() : Vault = {
		val vaultConfig = new VaultConfig().address(vaultEndpoint).build()
		new Vault(vaultConfig)
	}
}

class SimpleScalaRestApiConfig(var endpoint: String, var password: String)
