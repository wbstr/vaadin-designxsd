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
package com.wcs.maven.claraxsd;

import com.vaadin.ui.Component;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author kumm
 */
public class GeneratedSchema {

    private final Package componentPackage;
    private final XmlSchemaObjectCollection allGroupItems;
    private final XmlSchema schema;
    private final XmlSchemaObjectCollection items;
    private final ElementBuilderFactory elementBuilderFactory;
    private final Map<String, XmlSchemaElement> generatedElementsMap;

    public GeneratedSchema(
            Package componentPackage,
            ElementBuilderFactory elementBuilderFactory) {
        this.componentPackage = componentPackage;
        this.elementBuilderFactory = elementBuilderFactory;
        schema = buildSchemaFromTemplate();
        items = schema.getItems();
        allGroupItems = findAllGroupItems();
        generatedElementsMap = new TreeMap<>();
    }

    private XmlSchema buildSchemaFromTemplate() {
        XmlSchema template = SchemaHandler.load(getClass().getResourceAsStream("clara-template.xsd"));
        template.setTargetNamespace(NamingRules.getGeneratedXsdNamespace(componentPackage));
        return template;
    }

    private XmlSchemaObjectCollection findAllGroupItems() {
        XmlSchemaGroup allGroup = (XmlSchemaGroup) schema.getGroups().getValues().next();
        XmlSchemaGroupBase allGroupParticle = allGroup.getParticle();
        return allGroupParticle.getItems();
    }

    public void append(Class<? extends Component> componentClass) {
        ElementBuilder elementBuilder = elementBuilderFactory.getElementBuilder(componentClass);
        XmlSchemaElement element = elementBuilder.buildElement(schema, componentClass);
        if (element != null) {
            generatedElementsMap.put(element.getName(), element);
        }
    }

    private void appendToAllGroup(String name) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setRefName(new QName(schema.getTargetNamespace(), name));
        allGroupItems.add(element);
    }

    private void injectGeneratedContent() {
        for(XmlSchemaElement generatedElement : generatedElementsMap.values()) {
            items.add(generatedElement);
            appendToAllGroup(generatedElement.getName());
        }
    }

    public void write(Writer writer) {
        injectGeneratedContent();
        SchemaHandler.write(schema, writer);
    }

    public void writeUnFormatted(Writer writer) {
        injectGeneratedContent();
        SchemaHandler.writeUnFormatted(schema, writer);
    }

    public Package getComponentPackage() {
        return componentPackage;
    }
    
    public boolean isEmpty() {
        return generatedElementsMap.isEmpty();
    }
}
