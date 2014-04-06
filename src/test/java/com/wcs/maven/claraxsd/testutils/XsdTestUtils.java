/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.testutils;

import com.wcs.maven.claraxsd.GeneratedSchema;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaElement;

import javax.xml.transform.OutputKeys;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kumm
 */
public class XsdTestUtils {

    private static final Map<String,String> writeOptions = new HashMap<String,String>() {
        {
            put(OutputKeys.INDENT, "no");
        }
    };

    public static String readMarkup(GeneratedSchema generatedSchema) {
        StringWriter markupWriter = new StringWriter();
        generatedSchema.write(markupWriter, writeOptions);
        return markupWriter.toString();
    }

    public static String readGeneratedElementsMarkup(GeneratedSchema generatedSchema) {
        String markup = readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("</xs:group>") + "</xs:group>".length();
        int endIndex = markup.indexOf("</xs:schema>");
        return markup.substring(beginIndex, endIndex);
    }

    public static String readGeneratedAllComponentsGroupMarkup(GeneratedSchema generatedSchema) {
        String markup = XsdTestUtils.readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("<xs:group ");
        int endIndex = markup.indexOf("</xs:group>") + 11;
        return markup.substring(beginIndex, endIndex);
    }

    public static String buildAttributeMarkup(XmlSchema emptySchema, XmlSchemaAttribute attr) {
        emptySchema.getItems().add(attr);
        StringWriter markupWriter = new StringWriter();
        emptySchema.write(markupWriter, writeOptions);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<attribute ");
        return markup.substring(beginIndex, endIndex);
    }

    public static String buildElementMarkup(XmlSchema emptySchema, XmlSchemaElement el) {
        emptySchema.getItems().add(el);
        StringWriter markupWriter = new StringWriter();
        emptySchema.write(markupWriter, writeOptions);
        String markup = markupWriter.toString();
        int endIndex = markup.indexOf("</schema>");
        int beginIndex = markup.indexOf("<element ");
        return markup.substring(beginIndex, endIndex);
    }
}
