/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
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

    private final Package componentPackage;
    private final XmlSchemaObjectCollection allGroupItems;
    private final XmlSchema schema;
    private final XmlSchemaObjectCollection items;
    private final SchemaGenerator schemaGenerator;

    public GeneratedSchema(SchemaGenerator schemaGenerator, Package componentPackage) {
        this.componentPackage = componentPackage;
        this.schemaGenerator = schemaGenerator;
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
                generated.append(line.replace("${package}", componentPackage.getName())).append(System.lineSeparator());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return schemaGenerator.read(generated.toString());
    }

    private XmlSchemaObjectCollection findAllGroupItems() {
        XmlSchemaGroup allGroup = (XmlSchemaGroup) schema.getGroups().getValues().next();
        XmlSchemaGroupBase allGroupParticle = allGroup.getParticle();
        return allGroupParticle.getItems();
    }

    public void append(Class componentClass) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setName(componentClass.getSimpleName());
        final XmlSchemaComplexType type = new XmlSchemaComplexType(schema);
        element.setType(type);
        SchemaGenerator.AttributeGroup attributeGroup = schemaGenerator.findAttributeGroup(componentClass);
        final XmlSchemaObjectCollection typeAttributes = type.getAttributes();
        typeAttributes.add(attributeGroup.newRef());
        if (ComponentContainer.class.isAssignableFrom(componentClass)) {
            type.setParticle(newAllGroupRef(Long.MAX_VALUE));
        } else if (SingleComponentContainer.class.isAssignableFrom(componentClass)) {
            type.setParticle(newAllGroupRef(1));
        }
        items.add(element);
        appendAttributes(componentClass, attributeGroup, typeAttributes);
        appendToAllGroup(element.getName());
    }

    private void appendAttributes(Class componentClass, SchemaGenerator.AttributeGroup attributeGroup, final XmlSchemaObjectCollection typeAttributes) {
        Map<String, XmlSchemaAttribute> generatedAttributes = new TreeMap<>();
        for (Method method : componentClass.getMethods()) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 1 || !methodName.startsWith("set")) {
                continue;
            }
            String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
            if (attributeGroup.hasAttribute(propertyName)) {
                continue;
            }
            Class<?> parameterType = parameterTypes.length == 1 ? parameterTypes[0] : null;
            AttributeGenerator attributeGenerator = schemaGenerator.findAttributeGenerator(parameterType);
            if (attributeGenerator == null) {
                continue;
            }
            XmlSchemaAttribute attribute = attributeGenerator.generate(propertyName, parameterType);
            if (attribute != null) {
                generatedAttributes.put(propertyName, attribute);
            }
        }
        for (XmlSchemaAttribute attribute : generatedAttributes.values()) {
            typeAttributes.add(attribute);
        }
    }

    private XmlSchemaGroupRef newAllGroupRef(long maxOccurs) {
        XmlSchemaGroupRef allGroupRef = new XmlSchemaGroupRef();
        allGroupRef.setMinOccurs(0);
        allGroupRef.setMaxOccurs(maxOccurs);
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
