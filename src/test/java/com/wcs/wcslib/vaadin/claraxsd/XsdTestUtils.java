/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.wcslib.vaadin.claraxsd;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.OutputKeys;

/**
 *
 * @author kumm
 */
public class XsdTestUtils {

    private static final Map writeOptions = new HashMap() {
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

    public static String readGeneratedAttributesMarkup(GeneratedSchema generatedSchema) {
        String markup = readMarkup(generatedSchema);
        int beginIndex = markup.indexOf("<xs:complexType>") + "<xs:complexType>".length();
        int endIndex = markup.indexOf("</xs:complexType></xs:element>");
        return markup.substring(beginIndex, endIndex);
    }
}
