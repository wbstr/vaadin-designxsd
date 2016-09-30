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


import org.apache.ws.commons.schema.XmlSchema;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OutputFilesWriter {

    private final List<GeneratedSchema> generatedSchemas;
    private final Path destinationPath;
    private final XmlSchema mainXsd;

    public OutputFilesWriter(List<GeneratedSchema> generatedSchemas, String destination) {
        destinationPath = FileSystems.getDefault().getPath(destination);
        this.generatedSchemas = generatedSchemas;
        mainXsd = SchemaLoader.load(Generator.class.getResourceAsStream("main_template.xsd"));
    }

    public void writeFiles() throws IOException {
        Files.createDirectories(destinationPath);
        copyResource("design-html.xsd");
        copyResource("design-base.xsd");

        for (GeneratedSchema generatedSchema : generatedSchemas) {
            String destFileName = generatedSchema.getTagPrefix() + ".xsd";
            XmlSchema xmlSchema = generatedSchema.build();
            write(xmlSchema, destFileName);
            generatedSchema.includeToMain(mainXsd, destFileName);
        }

        write(mainXsd, "design.xsd");
    }

    private void write(XmlSchema xmlSchema, String fileName) throws IOException {
        Path destXsdPath = destinationPath.resolve(fileName);
        Writer writer = new FileWriter(destXsdPath.toFile(), false);
        xmlSchema.write(writer);
    }

    private void copyResource(String resource) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        Files.copy(resourceAsStream, destinationPath.resolve(resource));
    }

}
