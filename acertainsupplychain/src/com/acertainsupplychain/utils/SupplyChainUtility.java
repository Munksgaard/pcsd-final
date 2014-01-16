package com.acertainsupplychain.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class SupplyChainUtility {

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
