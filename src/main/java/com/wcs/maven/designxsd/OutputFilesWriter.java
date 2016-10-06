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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;

public class OutputFilesWriter {

    private final Path destinationPath;
    private final XmlSchema mainXsd;

    public OutputFilesWriter(String destination) {
        destinationPath = FileSystems.getDefault().getPath(destination);
        mainXsd = SchemaLoader.load("empty.xsd");

        XmlSchema htmlXsd = SchemaLoader.load("design-html.xsd");
        writeToMainXsd(htmlXsd);

        XmlSchema baseXsd = SchemaLoader.load("design-base.xsd");
        writeToMainXsd(baseXsd);
    }

    public final void writeToMainXsd(XmlSchema xmlSchema) {
        XmlSchemaObjectCollection items = xmlSchema.getItems();
        for (int i = 0; i < items.getCount(); i++) {
            mainXsd.getItems().add(items.getItem(i));
        }
    }

    public void wirteMainXsd() throws IOException {
        Path destXsdPath = destinationPath.resolve("design.xsd");
        Writer writer = new FileWriter(destXsdPath.toFile(), false);
        mainXsd.write(writer);
    }

}
