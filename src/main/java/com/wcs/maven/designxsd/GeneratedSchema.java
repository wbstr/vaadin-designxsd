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
import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author kumm
 */
public class GeneratedSchema {

    private final ElementBuilderFactory elementBuilderFactory;
    private final Map<String, XmlSchemaElement> generatedElementsMap;
    private final XmlSchema schema;

    public GeneratedSchema(ElementBuilderFactory elementBuilderFactory) {
        this.elementBuilderFactory = elementBuilderFactory;
        generatedElementsMap = new TreeMap<>();
        schema = SchemaLoader.load(getClass().getResourceAsStream("empty.xsd"));
    }

    public void append(Class<? extends Component> componentClass) {
        ElementBuilder elementBuilder = elementBuilderFactory.getElementBuilder(componentClass);
        XmlSchemaElement element = elementBuilder.buildElement(schema, componentClass);
        if (element != null) {
            generatedElementsMap.put(element.getName(), element);
        }
    }

    public String getTagPrefix() {
        final String firstName = generatedElementsMap.keySet().iterator().next();
        final String[] parts = firstName.split("-");
        return parts[0];
    }

    public boolean isEmpty() {
        return generatedElementsMap.isEmpty();
    }

    public XmlSchema build() {
        final XmlSchemaObjectCollection items = schema.getItems();
        generatedElementsMap.values().forEach(items::add);
        return schema;
    }

    public void includeToMain(XmlSchema mainXsd, String selfSchemaLocation) {
        final XmlSchemaObjectCollection allGroupItems = findAllGroupItems(mainXsd);
        generatedElementsMap.keySet().forEach(name -> appendToAllGroup(allGroupItems, name));
        XmlSchemaInclude include = new XmlSchemaInclude();
        include.setSchemaLocation(selfSchemaLocation);
        mainXsd.getItems().add(include);
    }

    private XmlSchemaObjectCollection findAllGroupItems(XmlSchema mainXsd) {
        XmlSchemaGroup allGroup = (XmlSchemaGroup) mainXsd.getGroups().getValues().next();
        XmlSchemaGroupBase allGroupParticle = allGroup.getParticle();
        return allGroupParticle.getItems();
    }

    private void appendToAllGroup(XmlSchemaObjectCollection allGroupItems, String name) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setRefName(new QName(name));
        allGroupItems.add(element);
    }

}
