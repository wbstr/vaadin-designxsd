/*
 * Copyright 2016 lali.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wcs.maven.designxsd.discoverer;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.declarative.DesignContext;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 *
 * @author lali
 */
public class ColGroupDiscoverer {

    public boolean discover(Component component) {
        if(Table.class.isAssignableFrom(component.getClass())) {
            Tag tableTag = Tag.valueOf(component.getClass().getSimpleName());
            Element tableElement = new Element(tableTag, "");
            
            Tag colgroupTag = Tag.valueOf("colgroup");
            Element colgroupElement = new Element(colgroupTag, "");
            tableElement.appendChild(colgroupElement);
            
            Tag colTag = Tag.valueOf("col");
            StubElement colElement = new StubElement(colTag, "");
            colgroupElement.appendChild(colElement);
            
            component.readDesign(colElement, new DesignContext());
            
            return colElement.searchItemId;
        }
        
        return false;
    }

    private class StubElement extends Element {

        boolean searchItemId;

        public StubElement(Tag tag, String baseUri, Attributes attributes) {
            super(tag, baseUri, attributes);
        }

        public StubElement(Tag tag, String baseUri) {
            super(tag, baseUri);
        }

        @Override
        public boolean hasAttr(String attributeKey) {
            searchItemId = true;
            return super.hasAttr(attributeKey);
        }
    }
}
