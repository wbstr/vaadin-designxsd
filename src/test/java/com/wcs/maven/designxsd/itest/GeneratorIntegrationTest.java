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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.DesignContext;
import com.wcs.maven.designxsd.GeneratedSchema;
import com.wcs.maven.designxsd.Generator;
import com.wcs.maven.designxsd.OutputFilesWriter;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kumm
 */
public class GeneratorIntegrationTest {

    @Test
    public void testGenerate() throws Exception {
        Path tempDirectory = Files.createTempDirectory("designxsd-test");
        String destination = tempDirectory.toString();
        System.out.println(destination);

        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(destination);
        outputFilesWriter.prepareDestination();
        Generator generator = new Generator(new Generator.GeneratedSchemaFactory(new DesignContext()));
        GeneratedSchema generatedSchema = generator.generate("com.vaadin.ui");
        outputFilesWriter.writeGeneratedXsd(generatedSchema, "vaadin");
        outputFilesWriter.wirteMainXsd();

/*        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Validator validator = factory.newSchema().newValidator();
        validator.setResourceResolver(catalogResolver);
        validator.validate(new StreamSource(getClass().getResourceAsStream("demo-layout.xml")));*/
    }

}
