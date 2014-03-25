/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wcs.wcslib.vaadin.claraxsd;

import org.apache.ws.commons.schema.XmlSchemaAttribute;

/**
 *
 * @author kumm
 */
public interface AttributeProducer {

    XmlSchemaAttribute produce(String name, Class<?> type);
    
    boolean isSupports(Class<?> type);
}
