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
import com.wcs.maven.designxsd.discoverer.AttributeDiscoverer;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ws.commons.schema.*;

/**
 *
 * @author kumm
 */
public class ComponentElementBuilder implements ElementBuilder {

    private static final Logger LOGGER = Logger.getLogger(ComponentElementBuilder.class.getName());

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
            element.setName(Design.getComponentMapper().componentToTag(component, designContext));
            XmlSchemaComplexType type = createElementType(component);
            element.setType(type);
            final XmlSchemaObjectCollection typeAttributes = type.getAttributes();
            for (BaseAttributeGroup baseAttributeGroup : attributeGroups) {
                typeAttributes.add(newAttributeGroupRef(baseAttributeGroup));
            }
            appendAttributes(componentClass, typeAttributes);
            return element;
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    "Component build skipped. Component name: "
                    + componentClass.getName(), e);
            return null;
        }
    }

    protected XmlSchemaComplexType createElementType(Component component) {
        return new XmlSchemaComplexType(schema);
    }

    private XmlSchemaAttributeGroupRef newAttributeGroupRef(BaseAttributeGroup baseAttributeGroup) {
        XmlSchemaAttributeGroupRef ref = new XmlSchemaAttributeGroupRef();
        ref.setRefName(baseAttributeGroup.getName());
        return ref;
    }

    private void appendAttributes(Class componentClass, XmlSchemaObjectCollection typeAttributes) throws InstantiationException, IllegalAccessException {
        Component component = (Component) componentClass.newInstance();
        AttributeDiscoverer attributeDiscoverer = new AttributeDiscoverer();
        Map<String, Class> attributes = attributeDiscoverer.discovery(component);

        for (Map.Entry<String, Class> entry : attributes.entrySet()) {
            String propertyName = entry.getKey();
            Class parameterType = entry.getValue();

            if (!hasAttributeGroup(propertyName)) {
                AttributeBuilder attributeBuilder = attributeBuilderFactory.getAttributeBuilder(propertyName, parameterType);
                XmlSchemaAttribute xmlSchemaAttribute = attributeBuilder.buildAttribute(schema, propertyName, parameterType);
                typeAttributes.add(xmlSchemaAttribute);
            }
        }
    }

    private boolean hasAttributeGroup(String propertyName) {
        for (BaseAttributeGroup attributeGroup : attributeGroups) {
            if (baseAttributeGroupMngr.isAttributeInherited(attributeGroup, propertyName)) {
                return true;
            }
        }

        return false;
    }
}
