package akkapmtsvc

import akka.actor.{ActorLogging, Actor}
import akkapmtsvc.domain.{APPROVED, DECLINED, AuthorizationResponse, AuthorizationRequest}

class AuthorizationClient extends Actor with ActorLogging {
  def receive = {
    case request: AuthorizationRequest =>
      request.CardNumber match {
        case "4111111111111111" => AuthorizationResponse(APPROVED)
        case _ => AuthorizationResponse(DECLINED)
      }
  }
}
