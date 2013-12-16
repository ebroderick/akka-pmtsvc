package akkapmtsvc

import akka.actor.{ActorLogging, Actor}
import org.xml.sax.{InputSource}
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import javax.xml.transform.dom.DOMSource

object MessageValidator {
  case class ValidationRequest(message: String)
  case class ValidationResponse(ok: Boolean)
}

class MessageValidator(schemaPath: String) extends Actor with ActorLogging {

  //set up validator
  val factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
  val cl = Thread.currentThread().getContextClassLoader()
  val schema = factory.newSchema(new StreamSource(cl.getResourceAsStream(schemaPath)))
  val validator = schema.newValidator()

  //set up documentBuilder
  val docBuilderFactory = DocumentBuilderFactory.newInstance()
  docBuilderFactory.setNamespaceAware(true)
  val docBuilder = docBuilderFactory.newDocumentBuilder()

  import MessageValidator._

  def receive = {
    case validationRequest: ValidationRequest => sender ! validate(validationRequest)
    case default => log.error(s"unexpected request: ${default.getClass.toString}")
  }

  def validate(request: ValidationRequest): ValidationResponse = {
    log.debug(s"validating request: ${request.message}")

    try {
      val is = new InputSource(new ByteArrayInputStream(request.message.getBytes("UTF-8")))
      val doc = docBuilder.parse(is)
      validator.validate(new DOMSource(doc))
      ValidationResponse(true)
    } catch {
      case ex: Exception =>
        log.error(ex, ex.getMessage)
        ValidationResponse(false)
    }
  }
}
