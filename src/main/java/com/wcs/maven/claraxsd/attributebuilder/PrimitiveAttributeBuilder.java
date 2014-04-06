/*
 * Copyright 2014 Webstar Csoport Kft.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.wcs.maven.claraxsd.attributebuilder;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

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
        attr.setSchemaTypeName(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName));
        return attr;
    }

    @Override
    public boolean isSupports(Class<?> type) {
        return type != null && (type.isPrimitive() || translationMap.containsKey(type));
    }

}
