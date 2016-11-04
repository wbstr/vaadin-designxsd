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

package com.wcs.maven.designxsd.attributebuilder;

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
        return attr;
    }

    @Override
    public boolean isSupports(Class<?> type) {
        return type == null;
    }
    
}
