package org.emb.pmt

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.pattern.{ask, pipe}
import akka.camel.{Consumer, CamelMessage}
import akka.util.Timeout
import scala.concurrent.duration._

//imports implicit ExecutionContext for Futures
import scala.concurrent.ExecutionContext.Implicits.global

class HttpConsumer extends Actor with ActorLogging with Consumer {
  //actorRef for the main PaymentProcessor actor
  val paymentProcessor: ActorRef = context.actorOf(Props[PaymentProcessor], "PaymentProcessor")

  //implicit timeout for ask pattern
  implicit val askTimeout = Timeout(10 seconds)

  //camel endpoint for this actor
  def endpointUri = "servlet:///pmtsvc"

  def receive = {
    //ask payment processor, pipe the response back to the sender
    case msg: CamelMessage =>
      val body = msg.bodyAs[String]
      log.debug("processing request:\n {}", Array(body))
      paymentProcessor ? body pipeTo sender

    case request: Any =>
      log.error("unexpected request: {}", Array(request.getClass.toString))
  }
}
