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
package com.wcs.maven.designxsd.testutils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 *
 * @author kumm
 */
public class XsdTestUtils {

    public static String readGeneratedElementsMarkup(XmlSchema generatedSchema, String... attributes) {
        StringWriter markupWriter = new StringWriter();
        writeUnFormatted(generatedSchema, markupWriter);
        String markup = markupWriter.toString();
        String startMarker = "<xs:element";
        for (String attribute : attributes) {
            startMarker = startMarker.concat(" ");
            startMarker = startMarker.concat(attribute);
        }
        int beginIndex = markup.indexOf(startMarker);
        int endIndex = markup.indexOf("</xs:schema>");
        return markup.substring(beginIndex, endIndex);
    }

    public static String readGeneratedAllComponentsGroupMarkup(XmlSchema schema) {
        StringWriter markupWriter = new StringWriter();
        writeUnFormatted(schema, markupWriter);
        String markup = markupWriter.toString();
        int beginIndex = markup.indexOf("<xs:group ");
        int endIndex = markup.indexOf("</xs:group>") + "</xs:group>".length();
        return markup.substring(beginIndex, endIndex);
    }

    public static String buildAttributeMarkup(XmlSchema emptySchema, XmlSchemaAttribute attr) {
        emptySchema.getItems().add(attr);
        StringWriter markupWriter = new StringWriter();
        writeUnFormatted(emptySchema, markupWriter);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<attribute ");
        return markup.substring(beginIndex, endIndex);
    }

    public static String buildElementMarkup(XmlSchema emptySchema, XmlSchemaElement el) {
        emptySchema.getItems().add(el);
        StringWriter markupWriter = new StringWriter();
        writeUnFormatted(emptySchema, markupWriter);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<element ");
        return markup.substring(beginIndex, endIndex);
    }

    private static final Map<String, String> UN_FORMATTED_WRITE_OPTIONS = new HashMap<String, String>() {
        {
            put(OutputKeys.INDENT, "no");
        }
    };

    private static void writeUnFormatted(XmlSchema schema, Writer writer) {
        schema.write(writer, UN_FORMATTED_WRITE_OPTIONS);
    }
}
