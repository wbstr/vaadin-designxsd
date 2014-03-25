/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author kumm
 */
class Generator {

    private XmlSchemaCollection schemaCol;
    private List<AttributeGroup> attributeGroups = new ArrayList<>();
    private Map<Package, GeneratedSchema> generatedSchemas = new HashMap<>();
    private Collection<AttributeProducer> attributeGenerators;

    public Generator() {
        this(Generator.class.getResourceAsStream("clara_base.xsd"));
    }

    public Generator(InputStream baseXsd) {
        this(baseXsd, new ArrayList<AttributeProducer>());
    }

    public Generator(InputStream baseXsd, Collection<AttributeProducer> attributeGenerators) {
        schemaCol = new XmlSchemaCollection();
        schemaCol.setSchemaResolver(new URIResolver() {

            @Override
            public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                return new InputSource(getClass().getResourceAsStream(schemaLocation));
            }
        });
        initAttributeGroups(schemaCol.read(new StreamSource(baseXsd), null));
        this.attributeGenerators = attributeGenerators;
    }

    private void initAttributeGroups(XmlSchema base) throws RuntimeException {
        Map<QName, AttributeGroup> attributeGroupsByName = new HashMap<>();
        for (Iterator it = base.getAttributeGroups().getValues(); it.hasNext();) {
            Object attributeGroup = it.next();
            if (attributeGroup instanceof XmlSchemaAttributeGroup) {
                AttributeGroup group = new AttributeGroup((XmlSchemaAttributeGroup) attributeGroup);
                attributeGroupsByName.put(group.getName(), group);
            } else {
                throw new RuntimeException("This is not an attributeGroup: " + attributeGroup);
            }
        }

        Set<QName> stored = new HashSet<>();
        while (!attributeGroupsByName.isEmpty()) {
            int beforeSize = stored.size();
            Iterator<Map.Entry<QName, AttributeGroup>> iterator = attributeGroupsByName.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<QName, AttributeGroup> entry = iterator.next();
                AttributeGroup group = entry.getValue();
                HashSet notStoredReference = new HashSet(group.getReferences());
                notStoredReference.removeAll(stored);
                if (notStoredReference.isEmpty()) {
                    attributeGroups.add(group);
                    iterator.remove();
                    stored.add(entry.getKey());
                }
            }
            if (beforeSize == stored.size() && !attributeGroupsByName.isEmpty()) {
                throw new RuntimeException("Loop detected between attributeGroups!");
            }
        }
        Collections.reverse(attributeGroups);
    }

    List<AttributeGroup> getAttributeGroups() {
        return attributeGroups;
    }

    public void append(Class componentClass) {
        getGeneratedSchema(componentClass.getPackage()).append(componentClass);
    }

    private GeneratedSchema getGeneratedSchema(Package componentPackage) {
        GeneratedSchema generatedSchema = generatedSchemas.get(componentPackage);
        if (generatedSchema == null) {
            generatedSchema = new GeneratedSchema(this, componentPackage);
            generatedSchemas.put(componentPackage, generatedSchema);
        }
        return generatedSchema;
    }

    public XmlSchema read(String newSchema) {
        return schemaCol.read(new StringReader(newSchema), null);
    }

    public AttributeGroup findAttributeGroup(Class componentClass) {
        for (AttributeGroup knownAttributeGroup : attributeGroups) {
            if (knownAttributeGroup.isAppliesTo(componentClass)) {
                return knownAttributeGroup;
            }
        }
        return null;
    }

    public AttributeProducer findAttributeGenerator(Class<?> parameterType) {
        for (AttributeProducer attributeGenerator : attributeGenerators) {
            if (attributeGenerator.isSupports(parameterType)) {
                return attributeGenerator;
            }
        }
        return null;
    }

    public Collection<GeneratedSchema> getGeneratedSchemas() {
        return generatedSchemas.values();
    }

    static class AttributeGroup {

        private Class groupClass;
        private List<String> attributes = new ArrayList<>();
        private QName name;
        private List<QName> references = new ArrayList();

        public AttributeGroup(XmlSchemaAttributeGroup attrGroup) {
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

}
