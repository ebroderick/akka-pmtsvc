package org.emb.pmt

import akka.actor.{ActorSystem}
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest.{WordSpecLike, BeforeAndAfterAll}


class PaymentProcessorSpec extends TestKit(ActorSystem("PaymentProcessorSpec"))
    with WordSpecLike
    with ImplicitSender
    with BeforeAndAfterAll {

  override def afterAll() { system.shutdown() }

  "PaymentProcessor" should {
    "receive messages" in {
      val actor = TestActorRef[PaymentProcessor]
      val xml = <AuthorizationRequest xmlns="http://pmt.emb.org/authorization" xmlns:tns="http://pmt.emb.org/authorization"
                    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <CardNumber>4111111111111111</CardNumber>
                  <ExpirationDate>2014-02</ExpirationDate>
                </AuthorizationRequest>

      actor ! xml.toString

      expectMsg("<AuthorizationResponse xmlns=\"http://pmt.emb.org/authorization\" " +
        "xmlns:tns=\"http://pmt.emb.org/authorization\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<ResponseCode>APPROVED</ResponseCode></AuthorizationResponse>")
    }
    "reject malformed messages" in {
      val actor = TestActorRef[PaymentProcessor]
      val xml = <InvalidAuthorizationRequest/>

      actor ! xml.toString

      expectMsg("<ErrorResponse xmlns=\"http://pmt.emb.org/authorization\" " +
        "xmlns:tns=\"http://pmt.emb.org/authorization\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<Error>Request failed schema validation</Error></ErrorResponse>")
    }
  }
}
