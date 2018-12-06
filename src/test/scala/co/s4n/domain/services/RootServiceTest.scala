package co.s4n.domain.services

import org.scalatest.FunSuite

class RootServiceTest extends FunSuite {

  test("The Root Service should return RootService in a h1 tag while serving...") {
    val result = RootService.serve()
    assert(result == "<h1>RootService</h1>")
  }

}