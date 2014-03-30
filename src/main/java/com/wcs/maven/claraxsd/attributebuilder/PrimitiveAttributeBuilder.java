/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.attributebuilder;

import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.SchemaLoader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;

/**
 *
 * @author kumm
 */
public class PrimitiveAttributeBuilder implements AttributeBuilder {

    private final Map<Class, String> translationMap = new HashMap<Class, String>() {
        {
            put(String.class, "string");
            put(Object.class, "string");
            put(Boolean.class, "boolean");
            put(Integer.class, "int");
            put(Long.class, "long");
            put(Byte.class, "byte");
            put(Float.class, "float");
            put(Double.class, "double");
        }
    };

    @Override
    public XmlSchemaAttribute buildAttribute(XmlSchema schema, String name, Class<?> type) {
        String typeName;
        if (type.isPrimitive()) {
            typeName = type.getName();
        } else {
            typeName = translationMap.get(type);
        }
        if (typeName == null) {
            return null;
        }
        XmlSchemaAttribute attr = new XmlSchemaAttribute();
        attr.setName(name);
        attr.setSchemaTypeName(new QName(SchemaLoader.SCHEMA_NS, typeName));
        return attr;
    }

    @Override
    public boolean isSupports(Class<?> type) {
        return type != null && (type.isPrimitive() || translationMap.containsKey(type));
    }

}