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

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kumm
 */
public class SchemaLoader {

    private static final Map<String,String> unFormattedWriteOptions = new HashMap<String,String>() {
        {
            put(OutputKeys.INDENT, "no");
        }
    };

    private SchemaLoader() {
    }

    public static XmlSchema load(InputStream xsdStream) {
        return new XmlSchemaCollection().read(new StreamSource(xsdStream), null);
    }

    public static void write(XmlSchema schema, Writer writer) {
        resolveSystemIds(schema);
        schema.write(writer);
    }

    public static void writeUnFormatted(XmlSchema schema, Writer writer) {
        resolveSystemIds(schema);
        schema.write(writer, unFormattedWriteOptions);
    }

    private static void resolveSystemIds(XmlSchema schema) {
        XmlSchemaObjectCollection items = schema.getItems();
        for (Iterator i = items.getIterator();i.hasNext();) {
            Object item = i.next();
            if (item instanceof XmlSchemaImport) {
                XmlSchemaImport schemaImport = (XmlSchemaImport) item;
                String systemId = NamingRules.resolveNamespaceToSystemId(schemaImport.getNamespace());
                schemaImport.setSchemaLocation(systemId);
            }
        }
    }

}
