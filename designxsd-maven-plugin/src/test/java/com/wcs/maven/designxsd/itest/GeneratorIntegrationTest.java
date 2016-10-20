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
package com.wcs.maven.designxsd.itest;

import com.vaadin.ui.Component;
import com.wcs.maven.designxsd.GeneratedSchema;
import com.wcs.maven.designxsd.Generator;
import com.wcs.maven.designxsd.OutputFilesWriter;
import java.util.Collection;
import java.util.Set;
import org.junit.Test;
import org.reflections.Reflections;

/**
 *
 * @author kumm
 */
public class GeneratorIntegrationTest {

    @Test
    public void testGenerate() throws Exception {
        String destination = "target";

        Reflections reflections = new Reflections("com.vaadin.ui");
        Set<Class<? extends Component>> allComponentClass = reflections.getSubTypesOf(Component.class);
        Generator generator = Generator.create(reflections, true);
        for (Class<? extends Component> componentClass : allComponentClass) {
            generator.generate(componentClass);
        }

        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        for (GeneratedSchema generatedSchema : generatedSchemas) {
            outputFilesWriter.appendToMainXsd(generatedSchema);
        }

        outputFilesWriter.wirteMainXsd();

        /*        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Validator validator = factory.newSchema().newValidator();
        validator.setResourceResolver(catalogResolver);
        validator.validate(new StreamSource(getClass().getResourceAsStream("demo-layout.xml")));*/
    }

}
