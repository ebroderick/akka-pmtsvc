package org.emb.pmt

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
  import MessageValidator._

  def receive = {
    case validationRequest: ValidationRequest => sender ! validate(validationRequest)
    case request: Any => log.error("unexpected request: {}", Array(request.getClass.toString))
  }

  def validate(request: ValidationRequest): ValidationResponse = {
    var validated = true

    log.debug("validating request: {}", Array(request.message))

    try {
      val schemaLang = "http://www.w3.org/2001/XMLSchema"
      val factory = SchemaFactory.newInstance(schemaLang)
      val cl = Thread.currentThread().getContextClassLoader()
      val schema = factory.newSchema(new StreamSource(cl.getResourceAsStream(schemaPath)))
      val validator = schema.newValidator()
      val docBuilderFactory = DocumentBuilderFactory.newInstance()

      docBuilderFactory.setNamespaceAware(true)

      val docBuilder = docBuilderFactory.newDocumentBuilder()
      val is = new InputSource(new ByteArrayInputStream(request.message.getBytes("UTF-8")))
      val doc = docBuilder.parse(is)

      validator.validate(new DOMSource(doc))

    } catch {
      case ex: Exception =>
        log.error(ex, ex.getMessage)
        validated = false
    }

    ValidationResponse(validated)
  }

}
