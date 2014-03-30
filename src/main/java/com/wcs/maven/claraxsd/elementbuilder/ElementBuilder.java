/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wcs.maven.claraxsd.elementbuilder;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;

/**
 *
 * @author kumm
 */
public interface ElementBuilder {

    public XmlSchemaElement buildElement(XmlSchema schema, Class componentClass);
}
