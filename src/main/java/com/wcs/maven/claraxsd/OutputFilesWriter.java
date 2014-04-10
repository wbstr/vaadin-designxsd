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


import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by kumm on 2014.04.10..
 */
public class OutputFilesWriter {

    Collection<GeneratedSchema> generatedSchemas;
    Path destinationPath;

    public OutputFilesWriter(Collection<GeneratedSchema> generatedSchemas, String destination) {
        destinationPath = FileSystems.getDefault().getPath(destination);
        this.generatedSchemas = generatedSchemas;
    }

    public void writeFiles() throws IOException {
        Files.createDirectories(destinationPath);
        for(NamingRules.FixedName fixed: NamingRules.FixedName.values()) {
            copyResource(fixed.getFileName());
        }
        Collection<Package> packages = new ArrayList<>(generatedSchemas.size());
        for (GeneratedSchema generatedSchema : generatedSchemas) {
            writeGeneratedSchema(generatedSchema);
            packages.add(generatedSchema.getComponentPackage());
        }
        writeCatalog(packages);
    }

    private void writeCatalog(Collection<Package> packages) throws IOException {
        Catalog catalog = new Catalog(packages, destinationPath);
        FileWriter catalogFW = new FileWriter(destinationPath.resolve(NamingRules.CATALOG_FILENAME).toFile());
        catalog.write(catalogFW);
        catalogFW.close();
    }

    private void copyResource(String resource) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        FileWriter fileWriter = new FileWriter(destinationPath.resolve(resource).toFile(), false);
        SchemaLoader.write(SchemaLoader.load(resourceAsStream), fileWriter);
    }

    private void writeGeneratedSchema(GeneratedSchema generatedSchema) throws IOException {
        String destFileName = NamingRules.getGeneratedXsdFileName(generatedSchema.getComponentPackage());
        Path destXsdPath = destinationPath.resolve(destFileName);
        Writer writer = new FileWriter(destXsdPath.toFile(), false);
        generatedSchema.write(writer);
    }

}
