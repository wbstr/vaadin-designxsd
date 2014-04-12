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

import com.wcs.maven.claraxsd.NamingRules.FixedName;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collection;

/**
 * @author kumm
 */
public class Catalog {

    private final Collection<Package> generatedSchemaPackages;
    private final Path basePath;

    public Catalog(Collection<Package> generatedSchemaPackages, Path basePath) {
        this.generatedSchemaPackages = generatedSchemaPackages;
        this.basePath = basePath;
    }

    public void write(Writer writer) throws IOException {
        writer.write(
                "<!DOCTYPE catalog PUBLIC \"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN\"\n"
                        + "         \"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd\">\n"
                        + "<catalog "
                        + "xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\" "
                        + "xml:base=\"" + basePath.toUri() + "\">\n"
        );
        for (FixedName fixed : NamingRules.FixedName.values()) {
            writer.write(buildSystemRow(fixed.getSystemId(), fixed.getFileName()));
        }
        for (Package componentPackage : generatedSchemaPackages) {
            String systemId = NamingRules.getGeneratedXsdSystemId(componentPackage);
            String fileName = NamingRules.getGeneratedXsdFileName(componentPackage);
            writer.write(buildSystemRow(systemId, fileName));
        }

        writer.write("</catalog>");
    }

    private String buildSystemRow(String systemId, String fileName) {
        return "<system systemId=\"" + systemId + "\" uri=\"" + fileName + "\"/>\n";
    }

}
