package akkapmtsvc

import spray.routing._
import spray.http._
import MediaTypes._
import com.typesafe.config.ConfigFactory

class Service extends HttpServiceActor {
  val config = ConfigFactory.load()

  def receive = runRoute(
    pathPrefix(config.getString("app.root-path")) {
      path("") {
        post {
          entity(as[String]) { request =>
            respondWithMediaType(`text/html`) {
              complete {
                request
              }
            }
          }
        }
      }
    })
}
