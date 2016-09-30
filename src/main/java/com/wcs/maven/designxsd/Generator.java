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
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.attributebuilder.AttributeBuilderFactory;
import com.wcs.maven.designxsd.baseattributegroup.BaseAttributeGroupMngr;
import com.wcs.maven.designxsd.baseattributegroup.BaseSchema;
import com.wcs.maven.designxsd.elementbuilder.ElementBuilderFactory;
import org.apache.ws.commons.schema.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author kumm
 */
public class Generator {

    private final Map<String, GeneratedSchema> generatedSchemas = new TreeMap<>();
    private final ElementBuilderFactory elementBuilderFactory;

    public static Generator create() {
        XmlSchema baseXsd = SchemaLoader.load(Generator.class.getResourceAsStream("design-base.xsd"));
        BaseSchema baseSchema = new BaseSchema(baseXsd);
        BaseAttributeGroupMngr baseAttributeGroupMngr = new BaseAttributeGroupMngr(baseSchema);
        AttributeBuilderFactory attributeBuilderFactory = new AttributeBuilderFactory();
        ElementBuilderFactory elementBuilderFactory
                = new ElementBuilderFactory(attributeBuilderFactory, baseAttributeGroupMngr, new DesignContext());
        return new Generator(elementBuilderFactory);
    }

    public Generator(ElementBuilderFactory elementBuilderFactory) {
        this.elementBuilderFactory = elementBuilderFactory;
    }

    public void generate(Class<? extends Component> componentClass) {
        getGeneratedSchema(componentClass.getPackage()).append(componentClass);
    }


    public List<GeneratedSchema> getGeneratedSchemas() {
        return generatedSchemas.values().stream()
                .filter(generatedSchema -> !generatedSchema.isEmpty())
                .sorted((o1, o2) -> o1.getTagPrefix().compareTo(o2.getTagPrefix()))
                .collect(Collectors.toList());
    }

    private GeneratedSchema getGeneratedSchema(Package componentPackage) {
        GeneratedSchema generatedSchema = generatedSchemas.get(componentPackage.getName());
        if (generatedSchema == null) {
            generatedSchema = new GeneratedSchema(elementBuilderFactory);
            generatedSchemas.put(componentPackage.getName(), generatedSchema);
        }
        return generatedSchema;
    }

}
