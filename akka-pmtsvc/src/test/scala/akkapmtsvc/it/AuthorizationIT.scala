package akkapmtsvc.it

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.Test
import scalaj.http.Http

class AuthorizationIT extends AssertionsForJUnit {

  @Test
  def testAuthorizationRequest() {
    try {
      val xml =
        <AuthorizationRequest xmlns="http://pmtsvc.org/authorization" xmlns:tns="http://pmtsvc.org/authorization"
            xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
          <CardNumber>4111111111111111</CardNumber>
          <ExpirationDate>2014-02</ExpirationDate>
        </AuthorizationRequest>

      val response = Http.postData("http://localhost:8080/akka-pmtsvc/services/pmtsvc", xml.toString())

      assertTrue(response.asString, response.asString.contains("AuthorizationResponse"))

    } catch {
      case e: Exception => e.printStackTrace(); fail(e)

    }
  }
}
