/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.SchemaLoader;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;

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
        restriction.setBaseTypeName(new QName(SchemaLoader.SCHEMA_NS, "string"));
        XmlSchemaObjectCollection facets = restriction.getFacets();
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
