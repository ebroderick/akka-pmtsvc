package akkapmtsvc

import akka.actor.{Props, ActorSystem}
import spray.servlet.WebBoot
import org.slf4j.LoggerFactory

class Boot extends WebBoot {
  val log = LoggerFactory.getLogger("Boot")

  log.info("starting actor system...")

  // we need an ActorSystem to host our application in
  val system = ActorSystem("tokenization-service")

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props[Service])

}
