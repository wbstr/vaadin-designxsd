/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kumm
 */
public class CatalogTest {

    @Test
    public void testWrite() throws Exception {
        StringWriter writer = new StringWriter();
        Path basePath = FileSystems.getDefault().getPath("/test", "path");

        Catalog instance = new Catalog(Arrays.asList(getClass().getPackage()), basePath);
        instance.write(writer);
        String result = writer.getBuffer().toString();
        String expected = "<!DOCTYPE catalog PUBLIC \"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN\"\n"
                + "         \"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd\">\n"
                + "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\n"
                + "<system systemId=\"clara://lib/base.xsd\" uri=\"file:///test/path/clara_base.xsd\"/>\n"
                + "<system systemId=\"clara://lib/parent.xsd\" uri=\"file:///test/path/clara_parent.xsd\"/>\n"
                + "<system systemId=\"clara://com.wcs.maven.claraxsd.xsd\" uri=\"file:///test/path/com.wcs.maven.claraxsd.xsd\"/>\n"
                + "</catalog>";
        assertEquals(expected, result);
    }

}
