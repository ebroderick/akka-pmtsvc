package org.emb.pmt

import domain.AuthorizationRequest;

object Application extends App {
  def printAuthRequestXml(authRequest: AuthorizationRequest) {
    println(scalaxb.toXML[AuthorizationRequest](authRequest.copy(), "AuthorizationRequest", domain.defaultScope))
  }

  val authRequest = new AuthorizationRequest("4111111111111111", "2014-02")
  printAuthRequestXml(authRequest)

  val xml = <AuthorizationRequest xmlns="http://pmt.emb.org/authorization" xmlns:tns="http://pmt.emb.org/authorization"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <CardNumber>4111111111111111</CardNumber>
              <ExpirationDate>2014-02</ExpirationDate>
            </AuthorizationRequest>

  val authRequest2 = scalaxb.fromXML[AuthorizationRequest](xml)
  printAuthRequestXml(authRequest2)
}
