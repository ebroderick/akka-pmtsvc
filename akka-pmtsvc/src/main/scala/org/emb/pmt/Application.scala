package org.emb.pmt

import akka.actor.{Props, ActorSystem}
import scalaj.http.Http
import akka.camel.CamelExtension
import scala.concurrent.duration._
import scala.concurrent.Await

object Application extends App {
  val system = ActorSystem("Payments")
  val camel = CamelExtension(system)
  val httpConsumer = system.actorOf(Props[HttpConsumer], "HttpConsumer")

  val activationFuture = camel.activationFutureFor(httpConsumer)(timeout = 10 seconds,
    executor = system.dispatcher)

  val result = Await.result(activationFuture, 10 seconds)

  val xml = <AuthorizationRequest xmlns="http://pmt.emb.org/authorization" xmlns:tns="http://pmt.emb.org/authorization"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <CardNumber>4111111111111111</CardNumber>
              <ExpirationDate>2014-02</ExpirationDate>
            </AuthorizationRequest>

  try {
    println("sending request")
    val response = Http.postData("http://localhost:8811/httpConsumer", xml.toString())

    println("result was " + response.asString)

  } catch {
    case e: Exception => e.printStackTrace()
  }
  Thread.sleep(10000)
  system.shutdown()
}
