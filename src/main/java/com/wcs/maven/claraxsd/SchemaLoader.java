/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import java.io.InputStream;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author kumm
 */
public class SchemaLoader {
    public static final String SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

    private XmlSchemaCollection schemaCol;

    public SchemaLoader() {
        schemaCol = new XmlSchemaCollection();
        schemaCol.setSchemaResolver(new URIResolver() {

            @Override
            public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                return new InputSource(getClass().getResourceAsStream(schemaLocation));
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
