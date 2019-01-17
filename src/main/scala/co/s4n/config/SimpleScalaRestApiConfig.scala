package co.s4n.config;

import com.ecwid.consul.v1.ConsulClient

object SimpleScalaRestApiConfig {
	val environment : String = sys.env.get("ENVIRONMENT").get
	val consulClient = new ConsulClient("localhost")
	var config : SimpleScalaRestApiConfig = init
	
	def apply(endpoint: String) : SimpleScalaRestApiConfig = new SimpleScalaRestApiConfig(endpoint)
	def apply() : SimpleScalaRestApiConfig = config

	def init() : SimpleScalaRestApiConfig = {
		val endpoint = consulClient.getKVValue(s"$environment/endpoint").getValue.getDecodedValue;
		SimpleScalaRestApiConfig(endpoint);
	}

	def reload() {
		config = init
	}
}

class SimpleScalaRestApiConfig(var endpoint: String)