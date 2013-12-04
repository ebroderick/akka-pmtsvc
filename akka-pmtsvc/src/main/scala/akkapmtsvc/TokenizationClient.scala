package akkapmtsvc

import akka.actor.{ActorLogging, Actor}
import scala.Array

object TokenizationClient {
  case class TokenizationRequest(primaryAccountNumber: String)
  case class TokenizationResponse(token: String)
}

class TokenizationClient extends Actor with ActorLogging {
  import TokenizationClient._

  def receive = {
    case tokenizationRequest: TokenizationClient => sender ! TokenizationResponse("token")
    case request: Any => log.error("unexpected request: {}", Array(request.getClass.toString))
  }
}
