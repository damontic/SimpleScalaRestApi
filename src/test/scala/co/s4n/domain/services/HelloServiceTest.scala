package co.s4n.domain.services

import org.scalatest.FunSuite

class HelloServiceTest extends FunSuite {

  test("The Hello Service should return HelloService in a h1 tag while serving...") {
    val result = HelloService.serve()
    assert(result == "<h1>HelloService</h1>")
  }

}