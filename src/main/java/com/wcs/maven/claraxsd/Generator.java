/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import static com.wcs.maven.claraxsd.NamingRules.FixedName.BASE;
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.claraxsd.baseattributegroup.BaseSchema;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import java.util.LinkedList;
import java.util.List;
import org.apache.ws.commons.schema.XmlSchema;

/**
 *
 * @author kumm
 */
public class Generator {

    private final Map<Package, GeneratedSchema> generatedSchemas = new HashMap<>();
    private final ElementBuilderFactory elementBuilderFactory;
    private final SchemaLoader schemaLoader;

    public static Generator create() {
        SchemaLoader schemaLoader = new SchemaLoader();
        XmlSchema baseXsd = schemaLoader.load(Generator.class.getResourceAsStream(BASE.getFileName()));
        BaseSchema baseSchema = new BaseSchema(baseXsd);
        BaseAttributeGroupMngr baseAttributeGroupMngr = new BaseAttributeGroupMngr(baseSchema);
        AttributeBuilderFactory attributeBuilderFactory = new AttributeBuilderFactory();
        ElementBuilderFactory elementBuilderFactory
                = new ElementBuilderFactory(attributeBuilderFactory, baseAttributeGroupMngr);
        return new Generator(elementBuilderFactory, schemaLoader);
    }

    public Generator(ElementBuilderFactory elementBuilderFactory, SchemaLoader schemaLoader) {
        this.elementBuilderFactory = elementBuilderFactory;
        this.schemaLoader = schemaLoader;
    }

    public void generate(Class componentClass) {
        getGeneratedSchema(componentClass.getPackage()).append(componentClass);
    }

    public Collection<GeneratedSchema> getGeneratedSchemas() {
        Collection<GeneratedSchema> generatedSchemaList = generatedSchemas.values();
        List<GeneratedSchema> result = new LinkedList<>();
        for (GeneratedSchema generatedSchema : generatedSchemaList) {
            if (!generatedSchema.isEmpty()) {
                result.add(generatedSchema);
            }
        }
        return result;
    }

    private GeneratedSchema getGeneratedSchema(Package componentPackage) {
        GeneratedSchema generatedSchema = generatedSchemas.get(componentPackage);
        if (generatedSchema == null) {
            generatedSchema = new GeneratedSchema(componentPackage, schemaLoader, elementBuilderFactory);
            generatedSchemas.put(componentPackage, generatedSchema);
        }
        return generatedSchema;
    }

}
