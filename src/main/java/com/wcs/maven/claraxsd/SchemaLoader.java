/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import com.wcs.maven.claraxsd.NamingRules.FixedName;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;

/**
 *
 * @author kumm
 */
public class SchemaLoader {

    private final XmlSchemaCollection schemaCol;

    public SchemaLoader() {
        schemaCol = new XmlSchemaCollection();
        schemaCol.setSchemaResolver(new URIResolver() {

            @Override
            public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                for (FixedName fixed : FixedName.values()) {
                    if (fixed.getSystemId().equals(schemaLocation)) {
                        return new InputSource(getClass().getResourceAsStream(fixed.getFileName()));
                    }
                }
                return null;
            }
        });
    }

    public XmlSchema load(String markup) {
        return schemaCol.read(new StringReader(markup), null);
    }

    public XmlSchema load(InputStream xsdStream) {
        return schemaCol.read(new StreamSource(xsdStream), null);
    }

}
