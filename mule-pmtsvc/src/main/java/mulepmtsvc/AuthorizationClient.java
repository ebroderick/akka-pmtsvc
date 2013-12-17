package mulepmtsvc;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;
import org.pmtsvc.authorization.AuthorizationRequest;
import org.pmtsvc.authorization.AuthorizationResponse;

public class AuthorizationClient implements Callable {

    @Override
    public Object onCall(MuleEventContext muleEventContext) throws Exception {
        MuleMessage muleMessage = muleEventContext.getMessage();
        AuthorizationRequest request = (AuthorizationRequest) muleMessage.getPayload();
        String token = muleMessage.getProperty(TokenizationClient.TOKEN_PROPERTY, PropertyScope.INVOCATION);
        AuthorizationResponse response = new AuthorizationResponse();
        String responseCode;

        if ("4111111111111111".equals(request.getCardNumber())) {
            responseCode = "APPROVED";
        } else {
            responseCode = "DECLINED";
        }

        response.setToken(token);
        response.setResponseCode(responseCode);
        muleMessage.setPayload(response);

        return muleMessage;
    }
}
