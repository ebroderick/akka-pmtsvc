package mulepmtsvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XsdValidationFilter implements Filter {
    private DocumentBuilder documentBuilder;
    private Validator validator;

    public XsdValidationFilter() throws SAXException, ParserConfigurationException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Schema schema = factory.newSchema(new StreamSource(cl.getResourceAsStream("Authorization.xsd")));
        validator = schema.newValidator();

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        documentBuilder = docBuilderFactory.newDocumentBuilder();
    }

    @Override
    public boolean accept(MuleMessage muleMessage) {
        try {
            validate((String) muleMessage.getPayload());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void validate(String message) throws SAXException, IOException {
        Document doc;

        //DocumentBuilder is not thread safe
        synchronized (documentBuilder) {
            doc = documentBuilder.parse(new InputSource(new ByteArrayInputStream(message.getBytes("UTF-8"))));
        }

        if (doc != null) {
            //Validator is not thread safe
            synchronized (validator) {
                validator.validate(new DOMSource(doc));
            }
        }
    }
}
