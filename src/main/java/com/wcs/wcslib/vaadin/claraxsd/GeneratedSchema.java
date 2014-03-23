/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import com.vaadin.ui.ComponentContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;

/**
 *
 * @author kumm
 */
class GeneratedSchema {

    private Package componentPackage;
    private XmlSchemaObjectCollection allGroupItems;
    private XmlSchema schema;
    private XmlSchemaObjectCollection items;
    private SchemaGenerator schemaGenerator;

    public GeneratedSchema(SchemaGenerator schemaGenerator, Package componentPackage) {
        this.componentPackage = componentPackage;
        this.schemaGenerator = schemaGenerator;
        initSchema();
        items = schema.getItems();
        initAllGroup();
    }

    private void initSchema() {
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
                generated.append(line.replace("${package}", componentPackage.getName())).append(System.lineSeparator());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        schema = schemaGenerator.read(generated.toString());
    }

    private void initAllGroup() {
        XmlSchemaGroup allGroup = (XmlSchemaGroup) schema.getGroups().getValues().next();
        XmlSchemaGroupBase allGroupParticle = allGroup.getParticle();
        allGroupItems = allGroupParticle.getItems();
    }

    public void append(Class componentClass) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setName(componentClass.getSimpleName());
        XmlSchemaComplexType type = new XmlSchemaComplexType(schema);
        element.setType(type);
        SchemaGenerator.AttributeGroup attributeGroup = schemaGenerator.findAttributeGroup(componentClass);
        type.getAttributes().add(attributeGroup.newRef());
        if (ComponentContainer.class.isAssignableFrom(componentClass)) {
            type.setParticle(newAllGroupRef());
        }
        items.add(element);
        appendToAllGroup(element.getName());
        /*
         Set<Method> allMethods = ReflectionUtils.getAllMethods(componentClass,
         ReflectionUtils.withModifier(Modifier.PUBLIC),
         ReflectionUtils.withPrefix("set")
         );*/
    }

    private XmlSchemaGroupRef newAllGroupRef() {
        XmlSchemaGroupRef allGroupRef = new XmlSchemaGroupRef();
        allGroupRef.setMinOccurs(0);
        allGroupRef.setMaxOccurs(Long.MAX_VALUE);
        allGroupRef.setRefName(newQName("AllComponentsGroup"));
        return allGroupRef;
    }

    private void appendToAllGroup(String name) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setRefName(newQName(name));
        allGroupItems.add(element);
    }
    
    private QName newQName(String name) {
        return new QName(schema.getTargetNamespace(), name);
    }

    public void write(Writer writer) {
        schema.write(writer);
    }

    public void write(Writer writer, Map options) {
        schema.write(writer, options);
    }

}
