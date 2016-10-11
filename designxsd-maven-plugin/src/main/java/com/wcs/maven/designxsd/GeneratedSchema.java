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
package com.wcs.maven.designxsd;

import com.vaadin.ui.Component;
import com.wcs.maven.designxsd.elementbuilder.ElementBuilder;
import com.wcs.maven.designxsd.elementbuilder.ElementBuilderFactory;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.*;

/**
 *
 * @author kumm
 */
public class GeneratedSchema {

    public static final QName ALL_COMPONENT_GROUP_QNAME = new QName("AllComponentsGroup");

    private final ElementBuilderFactory elementBuilderFactory;
    private final Map<String, XmlSchemaElement> generatedElementsMap;
    private final XmlSchema schema;

    public GeneratedSchema(ElementBuilderFactory elementBuilderFactory) {
        this.elementBuilderFactory = elementBuilderFactory;
        generatedElementsMap = new TreeMap<>();
        schema = SchemaLoader.load("empty.xsd");
    }

    public void append(Class<? extends Component> componentClass) {
        ElementBuilder elementBuilder = elementBuilderFactory.getElementBuilder(componentClass);
        XmlSchemaElement element = elementBuilder.buildElement(schema, componentClass);
        if (element != null) {
            generatedElementsMap.put(element.getName(), element);
        }
    }

    public boolean isEmpty() {
        return generatedElementsMap.isEmpty();
    }

    public void appendXmlSchema(XmlSchema xmlSchema) {
        appendToAllGroup(xmlSchema);
        XmlSchemaObjectCollection items = xmlSchema.getItems();
        generatedElementsMap.values().forEach(items::add);
    }

    private void appendToAllGroup(XmlSchema xmlSchema) {
        XmlSchemaGroup allComponentGroup = findOrCreateAllComponentGroup(xmlSchema);
        XmlSchemaGroupBase particle = allComponentGroup.getParticle();
        XmlSchemaObjectCollection items = particle.getItems();
        generatedElementsMap.keySet().forEach(name -> appendToAllGroup(items, name));
    }

    private XmlSchemaGroup findOrCreateAllComponentGroup(XmlSchema xmlSchema) {
        XmlSchemaObjectTable groups = xmlSchema.getGroups();
        XmlSchemaGroup allComponentGroup = (XmlSchemaGroup) groups.getItem(ALL_COMPONENT_GROUP_QNAME);
        
        if (allComponentGroup == null) {
            allComponentGroup = new XmlSchemaGroup();
            allComponentGroup.setName(ALL_COMPONENT_GROUP_QNAME);
            allComponentGroup.setParticle(new XmlSchemaChoice());
            groups.add(ALL_COMPONENT_GROUP_QNAME, allComponentGroup);
            xmlSchema.getItems().add(allComponentGroup);
        }

        return allComponentGroup;
    }

    private void appendToAllGroup(XmlSchemaObjectCollection allGroupItems, String name) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setRefName(new QName(name));
        allGroupItems.add(element);
    }
}
