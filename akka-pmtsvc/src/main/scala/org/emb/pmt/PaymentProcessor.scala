package org.emb.pmt

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import akka.pattern.{ask, pipe}
import domain._
import org.emb.pmt.MessageValidator.{ValidationRequest, ValidationResponse}
import akka.util.Timeout
import scala.concurrent.duration._

//imports implicit ExecutionContext for Futures
import scala.concurrent.ExecutionContext.Implicits.global

class PaymentProcessor extends Actor with ActorLogging {
  //actorRef for the MessageValidator actor
  val messageValidator: ActorRef =
    context.actorOf(Props(classOf[MessageValidator], "Authorization.xsd"), "MessageValidator")

  //implicit timeout for ask pattern
  implicit val askTimeout = Timeout(10 seconds)

  def receive = {
    case requestString: String =>
      messageValidator ? ValidationRequest(requestString) map {validationResponse =>
        validationResponse match {

          case ValidationResponse(true) =>
            val authRequest = unmarshalAuthorizationRequest(requestString)
            val authResponse = authRequest.CardNumber match {
              case "4111111111111111" => AuthorizationResponse(APPROVED)
              case _ => AuthorizationResponse(DECLINED)
            }
            marshalAuthorizationResponse(authResponse)

          case _ => marshalErrorResponse(ErrorResponse("Request failed schema validation"))
        }

      } pipeTo sender

    case _ => marshalErrorResponse(ErrorResponse("Invalid Request"))
  }

  def unmarshalAuthorizationRequest(xmlString: String): AuthorizationRequest =
    scalaxb.fromXML[AuthorizationRequest](scala.xml.XML.loadString(xmlString))

  def marshalAuthorizationResponse(authResponse: AuthorizationResponse): String =
    scalaxb.toXML[AuthorizationResponse](authResponse.copy(), "AuthorizationResponse", defaultScope).toString

  def marshalErrorResponse(errorResponse: ErrorResponse): String =
    scalaxb.toXML[ErrorResponse](errorResponse.copy(), "ErrorResponse", defaultScope).toString

}
