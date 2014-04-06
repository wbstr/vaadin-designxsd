/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.vaadin.ui.Component;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilder;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import org.apache.ws.commons.schema.*;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kumm
 */
public class GeneratedSchema {

    private final SchemaLoader schemaLoader;
    private final Package componentPackage;
    private final XmlSchemaObjectCollection allGroupItems;
    private final XmlSchema schema;
    private final XmlSchemaObjectCollection items;
    private final ElementBuilderFactory elementBuilderFactory;

    public GeneratedSchema(
            Package componentPackage,
            SchemaLoader schemaLoader,
            ElementBuilderFactory elementBuilderFactory) {
        this.schemaLoader = schemaLoader;
        this.componentPackage = componentPackage;
        this.elementBuilderFactory = elementBuilderFactory;
        schema = buildSchemaFromTemplate();
        items = schema.getItems();
        allGroupItems = findAllGroupItems();
    }

    private XmlSchema buildSchemaFromTemplate() {
        StringBuilder generated = new StringBuilder();
        InputStream templateStream = getClass().getResourceAsStream("clara-template.xsd");
        BufferedReader r;
        try {
            r = new BufferedReader(new InputStreamReader(templateStream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        String line;
        try {
            while ((line = r.readLine()) != null) {
                generated.append(line.replace("${package}", componentPackage.getName()));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return schemaLoader.load(generated.toString());
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
            items.add(element);
            appendToAllGroup(element.getName());
        }
    }

    private void appendToAllGroup(String name) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setRefName(new QName(schema.getTargetNamespace(), name));
        allGroupItems.add(element);
    }

    public void write(Writer writer) {
        schema.write(writer);
    }

    public void write(Writer writer, Map options) {
        schema.write(writer, options);
    }

    public Package getComponentPackage() {
        return componentPackage;
    }
    
    public boolean isEmpty() {
        for (Iterator iterator = items.getIterator(); iterator.hasNext();) {
            if (iterator.next() instanceof XmlSchemaElement) {
                return false;
            }
        }
        return true;
    }
}
