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
package com.wcs.maven.designxsd.elementbuilder;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilder;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroup;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroupMngr;
import org.apache.ws.commons.schema.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author kumm
 */
public class ComponentElementBuilder implements ElementBuilder {

    private Collection<BaseAttributeGroup> attributeGroups;
    private XmlSchema schema;
    private final AttributeBuilderFactory attributeBuilderFactory;
    private final BaseAttributeGroupMngr baseAttributeGroupMngr;
    private final DesignContext designContext;

    public ComponentElementBuilder(AttributeBuilderFactory attributeBuilderFactory, BaseAttributeGroupMngr baseAttributeGroupMngr, DesignContext designContext) {
        this.attributeBuilderFactory = attributeBuilderFactory;
        this.baseAttributeGroupMngr = baseAttributeGroupMngr;
        this.designContext = designContext;
    }
    
    @Override
    public XmlSchemaElement buildElement(XmlSchema schema, Class<? extends Component> componentClass) {
        this.schema = schema;
        attributeGroups = baseAttributeGroupMngr.findAttributeGroup(componentClass);
        XmlSchemaElement element = new XmlSchemaElement();
        Component component;
        try {
            component = componentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        element.setName(Design.getComponentMapper().componentToTag(component, designContext));
        final XmlSchemaComplexType type = new XmlSchemaComplexType(schema);
        element.setType(type);
        final XmlSchemaObjectCollection typeAttributes = type.getAttributes();
        for (BaseAttributeGroup baseAttributeGroup : attributeGroups) {
            typeAttributes.add(newAttributeGroupRef(baseAttributeGroup));
        }
//        appendAttributes(componentClass, typeAttributes);
        return element;
    }
    
    private XmlSchemaAttributeGroupRef newAttributeGroupRef(BaseAttributeGroup baseAttributeGroup) {
        XmlSchemaAttributeGroupRef ref = new XmlSchemaAttributeGroupRef();
        ref.setRefName(baseAttributeGroup.getName());
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
        for (BaseAttributeGroup attributeGroup : attributeGroups) {
            if (baseAttributeGroupMngr.isAttributeInherited(attributeGroup, propertyName)) {
                return null;
            }
        }
        Class<?> parameterType = parameterTypes.length == 1 ? parameterTypes[0] : null;
        AttributeBuilder attributeBuilder = attributeBuilderFactory.getAttributeBuilder(propertyName, parameterType);
        return attributeBuilder.buildAttribute(schema, propertyName, parameterType);
    }

}
