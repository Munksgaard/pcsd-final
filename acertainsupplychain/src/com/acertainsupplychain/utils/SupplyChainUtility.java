package com.acertainsupplychain.utils;

import java.io.IOException;
import java.io.Reader;

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

}
