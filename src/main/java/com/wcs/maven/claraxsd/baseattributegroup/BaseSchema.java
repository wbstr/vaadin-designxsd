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

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kumm
 */
public class BaseSchema {

    private final Map<QName, BaseAttributeGroup> attrGroupsByName;

    public BaseSchema(XmlSchema schema) {
        attrGroupsByName = collectAttributeGroupsByName(schema);
    }

    public Map<QName, BaseAttributeGroup> getAttrGroupsByName() {
        return attrGroupsByName;
    }

    private Map<QName, BaseAttributeGroup> collectAttributeGroupsByName(XmlSchema base) throws RuntimeException {
        Map<QName, BaseAttributeGroup> attributeGroupsByName = new HashMap<>();
        for (Iterator it = base.getAttributeGroups().getValues(); it.hasNext();) {
            Object attributeGroup = it.next();
            if (attributeGroup instanceof XmlSchemaAttributeGroup) {
                BaseAttributeGroup group = new BaseAttributeGroup((XmlSchemaAttributeGroup) attributeGroup);
                attributeGroupsByName.put(group.getName(), group);
            } else {
                throw new RuntimeException("This is not an attributeGroup: " + attributeGroup);
            }
        }
        return attributeGroupsByName;
    }
}
