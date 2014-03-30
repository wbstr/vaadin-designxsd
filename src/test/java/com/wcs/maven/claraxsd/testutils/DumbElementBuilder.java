/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.maven.claraxsd.testutils;

import com.wcs.maven.claraxsd.elementbuilder.ElementBuilder;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 *
 * @author kumm
 */
public class DumbElementBuilder implements ElementBuilder {

    @Override
    public XmlSchemaElement buildElement(XmlSchema schema, Class componentClass) {
        XmlSchemaElement element = new XmlSchemaElement();
        element.setName(componentClass.getSimpleName());
        return element;
    }
    
}
