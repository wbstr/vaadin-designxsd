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
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class SchemaHandlerTest {

    @Test
    public void testLoad() throws Exception {
        XmlSchema schema = SchemaHandler.load(getClass().getResourceAsStream("clara_base.xsd"));
        StringWriter stringWriter = new StringWriter();
        SchemaHandler.writeUnFormatted(schema, stringWriter);
        String markup = stringWriter.toString();
        int beginIndex = markup.indexOf("<import")+"<import".length();
        int endIndex = markup.indexOf("/>", beginIndex);
        String importElementStr = markup.substring(beginIndex, endIndex);
        assertTrue(importElementStr.contains(" schemaLocation=\""+NamingRules.FixedName.PARENT.getSystemId()+"\""));
    }

}
