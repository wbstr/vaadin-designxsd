/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import org.apache.ws.commons.schema.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 *
 * @author kumm
 */
public class EnumAttributeBuilder implements AttributeBuilder {

    @Override
    public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> javaEnumType) {
        XmlSchemaAttribute attr = new XmlSchemaAttribute();
        attr.setName(name);
        XmlSchemaSimpleType type = new XmlSchemaSimpleType(schema);
        attr.setSchemaType(type);
        XmlSchemaSimpleTypeRestriction restriction = new XmlSchemaSimpleTypeRestriction();
        type.setContent(restriction);
        restriction.setBaseTypeName(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"));
        XmlSchemaObjectCollection facets = restriction.getFacets();
        @SuppressWarnings("unchecked")
        Enum[] constants = ((Class<Enum>)javaEnumType).getEnumConstants();
        for (Enum enumConstant : constants) {
            facets.add(new XmlSchemaEnumerationFacet(enumConstant.name(), false));
        }
        return attr;
    }

    @Override
    public boolean isSupports(Class<?> type) {
        return type != null && type.isEnum();
    }
    
}
