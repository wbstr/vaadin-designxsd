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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author kumm
 */
public class Generator {

    private XmlSchemaCollection schemaCol;
    private List<BaseAttributeGroup> attributeGroups = new ArrayList<>();
    private Map<Package, GeneratedSchema> generatedSchemas = new HashMap<>();
    private Collection<AttributeProducer> attributeProducers;

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
        this.attributeProducers = attributeGenerators;
        
        XmlSchema baseXmlSchema = schemaCol.read(new StreamSource(baseXsd), null);
        BaseSchema baseSchema = new BaseSchema(baseXmlSchema);
        attributeGroups = collectBaseAttributeGroups(baseSchema.getAttrGroupsByName());
    }

    private List<BaseAttributeGroup> collectBaseAttributeGroups(Map<QName, BaseAttributeGroup> allByName) {
        Set<QName> storedAttrGroupNames = new HashSet<>();
        LinkedList<BaseAttributeGroup> result = new LinkedList<>();

        while (!allByName.isEmpty()) {
            int beforeSize = storedAttrGroupNames.size();
            Iterator<Map.Entry<QName, BaseAttributeGroup>> iterator = allByName.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<QName, BaseAttributeGroup> entry = iterator.next();
                BaseAttributeGroup group = entry.getValue();
                HashSet notStoredReference = new HashSet(group.getReferences());
                notStoredReference.removeAll(storedAttrGroupNames);
                if (notStoredReference.isEmpty()) {
                    result.addFirst(group);
                    iterator.remove();
                    storedAttrGroupNames.add(entry.getKey());
                }
            }
            if (beforeSize == storedAttrGroupNames.size() && !allByName.isEmpty()) {
                throw new RuntimeException("Loop detected between attributeGroups!");
            }
        }
        return result;
    }

    List<BaseAttributeGroup> getAttributeGroups() {
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

    public BaseAttributeGroup findAttributeGroup(Class componentClass) {
        for (BaseAttributeGroup baseAttributeGroup : attributeGroups) {
            if (baseAttributeGroup.isAppliesTo(componentClass)) {
                return baseAttributeGroup;
            }
        }
        return null;
    }

    public AttributeProducer findAttributeGenerator(Class<?> parameterType) {
        for (AttributeProducer attributeGenerator : attributeProducers) {
            if (attributeGenerator.isSupports(parameterType)) {
                return attributeGenerator;
            }
        }
        return null;
    }

    public Collection<GeneratedSchema> getGeneratedSchemas() {
        return generatedSchemas.values();
    }


}
