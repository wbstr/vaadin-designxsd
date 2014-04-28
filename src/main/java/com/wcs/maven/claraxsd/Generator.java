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
import com.wcs.maven.claraxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.claraxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.claraxsd.baseattributegroup.BaseSchema;
import com.wcs.maven.claraxsd.elementbuilder.ElementBuilderFactory;
import org.apache.ws.commons.schema.XmlSchema;

import java.util.*;

import static com.wcs.maven.claraxsd.NamingRules.FixedName.BASE;

/**
 *
 * @author kumm
 */
public class Generator {

    private final Map<String, GeneratedSchema> generatedSchemas = new TreeMap<>();
    private final ElementBuilderFactory elementBuilderFactory;

    public static Generator create() {
        XmlSchema baseXsd = SchemaHandler.load(Generator.class.getResourceAsStream(BASE.getFileName()));
        BaseSchema baseSchema = new BaseSchema(baseXsd);
        BaseAttributeGroupMngr baseAttributeGroupMngr = new BaseAttributeGroupMngr(baseSchema);
        AttributeBuilderFactory attributeBuilderFactory = new AttributeBuilderFactory();
        ElementBuilderFactory elementBuilderFactory
                = new ElementBuilderFactory(attributeBuilderFactory, baseAttributeGroupMngr);
        return new Generator(elementBuilderFactory);
    }

    public Generator(ElementBuilderFactory elementBuilderFactory) {
        this.elementBuilderFactory = elementBuilderFactory;
    }

    public void generate(Class<? extends Component> componentClass) {
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
        GeneratedSchema generatedSchema = generatedSchemas.get(componentPackage.getName());
        if (generatedSchema == null) {
            generatedSchema = new GeneratedSchema(componentPackage, elementBuilderFactory);
            generatedSchemas.put(componentPackage.getName(), generatedSchema);
        }
        return generatedSchema;
    }

}
