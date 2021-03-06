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

import com.wcs.maven.designxsd.elementbuilder.ElementBuilder;
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
