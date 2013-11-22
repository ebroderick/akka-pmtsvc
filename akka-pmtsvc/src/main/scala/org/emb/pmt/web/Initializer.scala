package org.emb.pmt.web

import javax.servlet.{ServletContextEvent, ServletContextListener}
import akka.actor.{Props, ActorSystem}
import org.emb.pmt.HttpConsumer
import akka.camel.CamelExtension
import scala.concurrent.Await
import scala.concurrent.duration._

class Initializer extends ServletContextListener {
  lazy val system = ActorSystem("Payments")
  lazy val camel = CamelExtension(system)

  def contextInitialized(e: ServletContextEvent): Unit = {
    val httpConsumer = system.actorOf(Props[HttpConsumer], "HttpConsumer")

    val activationFuture = camel.activationFutureFor(httpConsumer)(timeout = 10 seconds,
      executor = system.dispatcher)

    Await.result(activationFuture, 10 seconds)
  }

  def contextDestroyed(e: ServletContextEvent): Unit = {
    camel.context.shutdown()

    system.shutdown()
    system.awaitTermination(30 seconds)
  }
}
