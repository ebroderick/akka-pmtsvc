package akkapmtsvc

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import akka.pattern.{ask, pipe}
import domain._
import akkapmtsvc.MessageValidator.{ValidationRequest, ValidationResponse}
import akka.util.Timeout
import scala.concurrent.duration._

//imports implicit ExecutionContext for Futures
import scala.concurrent.ExecutionContext.Implicits.global

class PaymentProcessor extends Actor with ActorLogging {
  //actorRef for the MessageValidator actor
  val messageValidator =
    context.actorOf(Props(classOf[MessageValidator], "Authorization.xsd"), "MessageValidator")

  //actorRef for the AuthorizationClient actor
  val authorizationClient = context.actorOf(Props[AuthorizationClient], "AuthorizationClient")

  //implicit timeout for ask pattern
  implicit val askTimeout = Timeout(10 seconds)

  def receive = {
    case requestString: String =>
      try {
        //unmarshal the request
        val authRequest = unmarshalAuthorizationRequest(requestString)

        //set up futures
        val validationFuture = (messageValidator ? ValidationRequest(requestString)).mapTo[ValidationResponse]
        val authorizationFuture = (authorizationClient ? authRequest).mapTo[AuthorizationResponse]

        //for comprehension for futures, run in parallel
        val result =
          for {
            validationResponse <- validationFuture
            authResponse <- authorizationFuture
          } yield createResponse(validationResponse, authResponse)

        //pipe the response back to the sender
        result pipeTo sender

      } catch {
        case ex: Exception => sender ! marshalErrorResponse(ErrorResponse("Invalid Request"))
      }

    case _ => sender ! marshalErrorResponse(ErrorResponse("Invalid Request"))
  }

  def createResponse(validationResponse: ValidationResponse, authResponse: AuthorizationResponse) =  {
    validationResponse match {
      case ValidationResponse(true) => marshalAuthorizationResponse(AuthorizationResponse(APPROVED))
      case _ => marshalErrorResponse(ErrorResponse("Request failed schema validation"))
    }
  }

  def unmarshalAuthorizationRequest(xmlString: String): AuthorizationRequest =
    scalaxb.fromXML[AuthorizationRequest](scala.xml.XML.loadString(xmlString))

  def marshalAuthorizationResponse(authResponse: AuthorizationResponse): String =
    scalaxb.toXML[AuthorizationResponse](authResponse.copy(), "AuthorizationResponse", defaultScope).toString

  def marshalErrorResponse(errorResponse: ErrorResponse): String =
    scalaxb.toXML[ErrorResponse](errorResponse.copy(), "ErrorResponse", defaultScope).toString
}
