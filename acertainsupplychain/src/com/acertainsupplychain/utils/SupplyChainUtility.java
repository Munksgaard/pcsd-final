package com.acertainsupplychain.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.servlet.http.HttpServletRequest;

public class SupplyChainUtility {

    public static Object extractRequest(HttpServletRequest request)
        throws IOException {

        // Extract XML
        Reader reader = request.getReader();
        int len = request.getContentLength();
        char buf[] = new char[len];
        reader.read(buf);
        reader.close();

        // Convert XML to object
        String str = new String(buf);
        XStream xmlStream = new XStream(new StaxDriver());
        return xmlStream.fromXML(str);
    }

    /**
     * Convert object to XML
     */
    public static String serializeObject(Object object) {
        XStream xmlStream = new XStream(new StaxDriver());
        return xmlStream.toXML(object);
    }

    /**
     * Convert object from XML
     */
    public static Object deserializeObject(String xmlObject) {
        XStream xmlStream = new XStream(new StaxDriver());
        return xmlStream.fromXML(xmlObject);
    }

    public static Object SendAndRecv(HttpClient client, ContentExchange exchange)
     throws IOException, InterruptedException, UnsupportedEncodingException,
            OrderProcessingException {
        int exchangeState;

        client.send(exchange);

        exchangeState = exchange.waitForDone();

        if (exchangeState == HttpExchange.STATUS_COMPLETED) {
            return SupplyChainUtility.deserializeObject(exchange.getResponseContent().trim());
        } else {
            throw new OrderProcessingException("Communication failed!");
        }
    }

}
