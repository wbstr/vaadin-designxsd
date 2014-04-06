/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.wcs.maven.claraxsd.NamingRules.FixedName;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collection;

/**
 *
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
                + "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\n");
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
        StringBuilder sb = new StringBuilder();
        String uri = basePath.resolve(fileName).toUri().toString();
        sb.append("<system systemId=\"")
                .append(systemId)
                .append("\" uri=\"")
                .append(uri)
                .append("\"/>\n");
        return sb.toString();
    }

}
