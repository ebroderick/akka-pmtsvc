package akkapmtsvc

import akka.actor.{ActorLogging, Actor}

object TokenizationClient {
  case class TokenizationRequest(primaryAccountNumber: String)
  case class TokenizationResponse(token: String)
}

class TokenizationClient extends Actor with ActorLogging {
  import TokenizationClient._

  def receive = {
    case tokenizationRequest: TokenizationRequest =>
      sender ! TokenizationResponse(tokenize(tokenizationRequest.primaryAccountNumber))

    case default => log.error(s"unexpected request: ${default}")
  }

  def tokenize(accountNumber: String) = {
    accountNumber.zipWithIndex.map {
      case (e, i) if i > 5 && i < 12 => 'X'
      case (e, i) => e
    }.mkString
  }
}
