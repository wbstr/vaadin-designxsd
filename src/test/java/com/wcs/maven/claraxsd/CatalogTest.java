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

import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author kumm
 */
public class CatalogTest {

    @Test
    public void testWrite() throws Exception {
        StringWriter writer = new StringWriter();
        Catalog instance = new Catalog(Arrays.asList(getClass().getPackage()));
        instance.write(writer);
        String result = writer.getBuffer().toString();
        String expected = "<!DOCTYPE catalog PUBLIC \"-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN\"\n"
                + "         \"http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd\">\n"
                + "<catalog xmlns=\"urn:oasis:names:tc:entity:xmlns:xml:catalog\">\n"
                + "<system systemId=\"clara://lib/base.xsd\" uri=\"clara_base.xsd\"/>\n"
                + "<system systemId=\"clara://lib/parent.xsd\" uri=\"clara_parent.xsd\"/>\n"
                + "<system systemId=\"clara://com.wcs.maven.claraxsd.xsd\" uri=\"com.wcs.maven.claraxsd.xsd\"/>\n"
                + "</catalog>";
        assertEquals(expected, result);
    }

}
