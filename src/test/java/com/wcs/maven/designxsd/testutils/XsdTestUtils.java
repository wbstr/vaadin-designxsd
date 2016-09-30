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

import com.wcs.maven.claraxsd.GeneratedSchema;
import com.wcs.maven.claraxsd.SchemaHandler;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaElement;

import java.io.StringWriter;

/**
 *
 * @author kumm
 */
public class XsdTestUtils {

    public static String readMarkup(GeneratedSchema generatedSchema) {
        StringWriter markupWriter = new StringWriter();
        generatedSchema.writeUnFormatted(markupWriter);
        return markupWriter.toString();
    }

    public static String readGeneratedElementsMarkup(XmlSchema generatedSchema) {
        StringWriter markupWriter = new StringWriter();
        SchemaHandler.writeUnFormatted(generatedSchema, markupWriter);
        String markup = markupWriter.toString();
        int beginIndex = markup.indexOf("<xs:element");
        int endIndex = markup.indexOf("</xs:schema>");
        return markup.substring(beginIndex, endIndex);
    }

    public static String readGeneratedAllComponentsGroupMarkup(XmlSchema schema) {
        StringWriter markupWriter = new StringWriter();
        SchemaHandler.writeUnFormatted(schema, markupWriter);
        String markup = markupWriter.toString();
        int beginIndex = markup.indexOf("<xs:group ");
        int endIndex = markup.indexOf("</xs:group>") + "</xs:group>".length();
        return markup.substring(beginIndex, endIndex);
    }

    public static String readGeneratedElementsMarkup(GeneratedSchema generatedSchema) {
        String markup = readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("</group>") + "</group>".length();
        int endIndex = markup.indexOf("</schema>");
        return markup.substring(beginIndex, endIndex);
    }

    public static String readGeneratedAllComponentsGroupMarkup(GeneratedSchema generatedSchema) {
        String markup = XsdTestUtils.readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("<group ");
        int endIndex = markup.indexOf("</group>") + "</group>".length();
        String str = markup.substring(beginIndex, endIndex);
        return str.replaceFirst("<annotation><documentation>[^<]*</documentation></annotation>", "");
    }

    public static String buildAttributeMarkup(XmlSchema emptySchema, XmlSchemaAttribute attr) {
        emptySchema.getItems().add(attr);
        StringWriter markupWriter = new StringWriter();
        SchemaHandler.writeUnFormatted(emptySchema, markupWriter);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<attribute ");
        return markup.substring(beginIndex, endIndex);
    }

    public static String buildElementMarkup(XmlSchema emptySchema, XmlSchemaElement el) {
        emptySchema.getItems().add(el);
        StringWriter markupWriter = new StringWriter();
        SchemaHandler.writeUnFormatted(emptySchema, markupWriter);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<element ");
        return markup.substring(beginIndex, endIndex);
    }
}
