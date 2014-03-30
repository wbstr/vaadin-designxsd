/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.baseattributegroup;

import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroup;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;

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
