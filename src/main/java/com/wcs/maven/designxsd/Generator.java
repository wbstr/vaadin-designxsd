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
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.*;

/**
 * @author kumm
 */
public class Generator {

    private final GeneratedSchemaFactory generatedSchemaFactory;

    public Generator(GeneratedSchemaFactory generatedSchemaFactory) {
        this.generatedSchemaFactory = generatedSchemaFactory;
    }

    public GeneratedSchema generate(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner());
        Set<Class<? extends Component>> allComponentClass = reflections.getSubTypesOf(Component.class);
        GeneratedSchema generatedSchema = generatedSchemaFactory.newGeneratedSchema();
        allComponentClass.stream()
                .filter(c -> c.getPackage().getName().equals(packageName)) //filter subpackages out
                .forEach(generatedSchema::append);
        return generatedSchema;
    }

    public static class GeneratedSchemaFactory {
        private final ElementBuilderFactory elementBuilderFactory;

        public GeneratedSchemaFactory(DesignContext designContext) {
            XmlSchema baseXsd = SchemaLoader.load(Generator.class.getResourceAsStream("design-base.xsd"));
            BaseSchema baseSchema = new BaseSchema(baseXsd);
            BaseAttributeGroupMngr baseAttributeGroupMngr = new BaseAttributeGroupMngr(baseSchema);
            AttributeBuilderFactory attributeBuilderFactory = new AttributeBuilderFactory();
            elementBuilderFactory
                    = new ElementBuilderFactory(attributeBuilderFactory, baseAttributeGroupMngr, designContext);
        }

        public GeneratedSchema newGeneratedSchema() {
            return new GeneratedSchema(elementBuilderFactory);
        }
    }

}
