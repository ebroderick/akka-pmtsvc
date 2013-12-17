package mulepmtsvc;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;
import org.pmtsvc.authorization.AuthorizationRequest;

public class TokenizationClient implements Callable {
    protected static final String TOKEN_PROPERTY = "token";

    @Override
    public Object onCall(MuleEventContext muleEventContext) throws Exception {
        MuleMessage muleMessage = muleEventContext.getMessage();
        AuthorizationRequest request = (AuthorizationRequest) muleMessage.getPayload();
        String cardNumber = request.getCardNumber();

        muleMessage.setProperty(TOKEN_PROPERTY, getToken(cardNumber), PropertyScope.INVOCATION);

        return muleMessage;
    }

    private String getToken(String cardNumber) {
        char[] result = new char[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 5 && i < 12) {
                result[i] = 'X';
            } else {
                result[i] = cardNumber.charAt(i);
            }
        }

        return new String(result);
    }
}
