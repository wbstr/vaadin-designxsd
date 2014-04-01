/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;

/**
 *
 * @author kumm
 */
public class NoValueAttributeBuilder implements AttributeBuilder {

    @Override
    public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> type) {
        XmlSchemaAttribute attr = new XmlSchemaAttribute();
        attr.setName(name);
        attr.setSchemaTypeName(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"));
        attr.setFixedValue("");
        return attr;
    }

    @Override
    public boolean isSupports(Class<?> type) {
        return type == null;
    }
    
}
