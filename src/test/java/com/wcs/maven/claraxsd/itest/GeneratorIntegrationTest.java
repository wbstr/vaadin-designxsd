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
package com.wcs.maven.claraxsd.itest;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wcs.maven.claraxsd.*;
import org.apache.xerces.util.XMLCatalogResolver;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kumm
 */
public class GeneratorIntegrationTest {

    @Test
    public void testGenerate() throws Exception {
        NamingRules.initBaseSystemIdUri("http://clara/itest/");
        Generator generator = Generator.create();
        generator.generate(Label.class);
        generator.generate(VerticalLayout.class);
        generator.generate(MyComponent.class);

        Collection<GeneratedSchema> generatedSchemas = generator.getGeneratedSchemas();
        assertEquals(2, generatedSchemas.size());

        Path tempDirectory = Files.createTempDirectory("claraxsd-test");
        OutputFilesWriter outputFilesWriter = new OutputFilesWriter(generatedSchemas, tempDirectory.toString());
        outputFilesWriter.writeFiles();

        String catalogPathStr = tempDirectory.resolve(NamingRules.CATALOG_FILENAME).toString();
        XMLCatalogResolver catalogResolver = new XMLCatalogResolver(new String[] {catalogPathStr});
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Validator validator = factory.newSchema().newValidator();
        validator.setResourceResolver(catalogResolver);
        validator.validate(new StreamSource(getClass().getResourceAsStream("demo-layout.xml")));
    }

}
