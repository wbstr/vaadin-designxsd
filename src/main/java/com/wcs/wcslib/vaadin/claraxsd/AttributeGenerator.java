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
public interface AttributeGenerator {
/*    private static final List<Class<?>> primitiveClasses = Arrays.asList(
            String.class, Object.class, Boolean.class, Integer.class,
            Byte.class, Short.class, Long.class, Character.class, Float.class,
            Double.class);*/

    XmlSchemaAttribute generate(String name, Class<?> type);
    
    boolean isSupports(Class<?> type);
}
