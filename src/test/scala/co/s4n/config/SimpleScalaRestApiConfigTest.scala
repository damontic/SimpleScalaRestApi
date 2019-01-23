package co.s4n.config

import org.scalatest.FunSuite

import java.net.Socket
import java.util.HashMap

import scala.util.{Try, Success, Failure}

import collection.JavaConverters._

import com.bettercloud.vault.{Vault, VaultConfig}

class SimpleScalaRestApiConfigTest extends FunSuite {
  test("Should throw exception if init() not previously called") {
    assertThrows[Exception] {
    	SimpleScalaRestApiConfig()
  	}
  }

  test("Should connect to Vault and put, get and delete values if vault is running locally") {
	Try(new Socket("127.0.0.1",8200)) match {
		case Success(s) => {
			val vaultLocalEndpoint = "http://127.0.0.1:8200"
			val vaultSecret = "secret/ssra"
			val vaultClient = new Vault(new VaultConfig().address(vaultLocalEndpoint).build())

			val secrets : HashMap[String, Object] = new HashMap[String, Object]()
			secrets.put("database_endpoint", "somedbendpoint.com:9543")
			secrets.put("database_password", "12345")
			vaultClient.logical().write(vaultSecret, secrets)

			SimpleScalaRestApiConfig.init(vaultLocalEndpoint, vaultSecret)
			val config = SimpleScalaRestApiConfig()
			assert(config.dbEndpoint == "somedbendpoint.com:9543")
    		assert(config.dbPassword == "12345")

			vaultClient.logical().delete(vaultSecret)

		}
		case Failure(e) => Unit
	}
  }
}
