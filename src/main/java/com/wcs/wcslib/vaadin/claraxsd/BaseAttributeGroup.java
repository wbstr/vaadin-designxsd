/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.wcslib.vaadin.claraxsd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;

/**
 *
 * @author kumm
 */
public class BaseAttributeGroup {
    private Class groupClass;
    private final List<String> attributes = new ArrayList<>();
    private final QName name;
    private final List<QName> references = new ArrayList();

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
            groupClass = Class.forName(name.getLocalPart());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Can't load class " + name.getLocalPart(), ex);
        }
    }

    public QName getName() {
        return name;
    }

    public Class getGroupClass() {
        return groupClass;
    }

    public boolean isAppliesTo(Class componentClass) {
        return groupClass.isAssignableFrom(componentClass);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public List<QName> getReferences() {
        return references;
    }

    public XmlSchemaAttributeGroupRef newRef() {
        XmlSchemaAttributeGroupRef ref = new XmlSchemaAttributeGroupRef();
        ref.setRefName(name);
        return ref;
    }
    
}
