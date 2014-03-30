/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.elementbuilder;

import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroup;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilder;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;

/**
 *
 * @author kumm
 */
public class ComponentElementBuilder implements ElementBuilder {

    private BaseAttributeGroup attributeGroup;
    private XmlSchema schema;
    private AttributeBuilderFactory attributeBuilderFactory;
    private BaseAttributeGroupMngr baseAttributeGroupMngr;

    public ComponentElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr) {
        this.attributeBuilderFactory = attributeBuilderFactory;
        this.baseAttributeGroupMngr = baseAttributeGroupMngr;
    }
    
    @Override
    public XmlSchemaElement buildElement(XmlSchema schema, Class componentClass) {
        this.schema = schema;
        attributeGroup = baseAttributeGroupMngr.findAttributeGroup(componentClass);
        XmlSchemaElement element = new XmlSchemaElement();
        element.setName(componentClass.getSimpleName());
        final XmlSchemaComplexType type = new XmlSchemaComplexType(schema);
        element.setType(type);
        final XmlSchemaObjectCollection typeAttributes = type.getAttributes();
        if (attributeGroup != null) {
            typeAttributes.add(newAttributeGroupRef());
        }
        appendAttributes(componentClass, typeAttributes);
        return element;
    }
    
    public XmlSchemaAttributeGroupRef newAttributeGroupRef() {
        XmlSchemaAttributeGroupRef ref = new XmlSchemaAttributeGroupRef();
        ref.setRefName(attributeGroup.getName());
        return ref;
    }

    private void appendAttributes(Class componentClass, XmlSchemaObjectCollection typeAttributes) {
        Map<String, XmlSchemaAttribute> generatedAttributes = new TreeMap<>();
        for (Method method : componentClass.getMethods()) {
            XmlSchemaAttribute attribute = buildAttribute(method);
            if (attribute != null) {
                generatedAttributes.put(attribute.getName(), attribute);
            }
        }
        for (XmlSchemaAttribute attribute : generatedAttributes.values()) {
            typeAttributes.add(attribute);
        }
    }

    private XmlSchemaAttribute buildAttribute(Method method) {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 1 || !methodName.startsWith("set")) {
            return null;
        }
        String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        if (baseAttributeGroupMngr.isAttributeInherited(attributeGroup, propertyName)) {
            return null;
        }
        Class<?> parameterType = parameterTypes.length == 1 ? parameterTypes[0] : null;
        AttributeBuilder attributeBuilder = attributeBuilderFactory.getAttributeBuilder(propertyName, parameterType);
        return attributeBuilder.buildAttribute(schema, propertyName, parameterType);
    }

}
