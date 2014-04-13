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

package com.wcs.maven.claraxsd.baseattributegroup;

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author kumm
 */
public class BaseAttributeGroup {
    private final Class<?> groupClass;
    private final List<String> attributes = new ArrayList<>();
    private final QName name;
    private final List<QName> references = new ArrayList<>();

    public BaseAttributeGroup(XmlSchemaAttributeGroup attrGroup) {
        for (Iterator it = attrGroup.getAttributes().getIterator(); it.hasNext();) {
            Object item = it.next();
            if (item instanceof XmlSchemaAttribute) {
                XmlSchemaAttribute attribute = (XmlSchemaAttribute) item;
                attributes.add(attribute.getName());
            } else if (item instanceof XmlSchemaAttributeGroupRef) {
                XmlSchemaAttributeGroupRef attrGroupRef = (XmlSchemaAttributeGroupRef) item;
                references.add(attrGroupRef.getRefName());
            } else {
                throw new RuntimeException("Unexpected attributeGroup item: " + item);
            }
        }
        name = attrGroup.getName();
        try {
            groupClass = Class.forName(name.getLocalPart().replace("..", "$"));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Can't load class " + name.getLocalPart(), ex);
        }
    }

    public QName getName() {
        return name;
    }

    public Class<?> getGroupClass() {
        return groupClass;
    }

    public boolean isAppliesTo(Class<?> componentClass) {
        return groupClass.isAssignableFrom(componentClass);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public List<QName> getReferences() {
        return references;
    }

}
