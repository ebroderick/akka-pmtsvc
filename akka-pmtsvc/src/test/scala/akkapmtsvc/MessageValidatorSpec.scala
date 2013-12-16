package akkapmtsvc

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest.{WordSpecLike, BeforeAndAfterAll}
import MessageValidator.{ValidationRequest, ValidationResponse}

class MessageValidatorSpec extends TestKit(ActorSystem("MessageValidatorSpec"))
    with WordSpecLike
    with ImplicitSender
    with BeforeAndAfterAll {

  override def afterAll() { system.shutdown() }

  "MessageValidator" should {
    "validate correct messages" in {
      val actor = TestActorRef(Props(classOf[MessageValidator], "Authorization.xsd"))
      val xml = <AuthorizationRequest xmlns="http://pmt.emb.org/authorization" xmlns:tns="http://pmt.emb.org/authorization"
                    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <CardNumber>4111111111111111</CardNumber>
                  <ExpirationDate>2014-02</ExpirationDate>
                </AuthorizationRequest>

      actor ! ValidationRequest(xml.toString())
      expectMsg(ValidationResponse(true))
    }
    "reject invalid messages" in {
      val actor = TestActorRef(Props(classOf[MessageValidator], "Authorization.xsd"))
      val xml = <ThisIsNotADefinedElement/>

      actor ! ValidationRequest(xml.toString())
      expectMsg(ValidationResponse(false))
    }
  }
}

