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

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {
    
    public boolean discover(Component component) {
        if (AbstractSelect.class.isAssignableFrom(component.getClass())) {
            Tag abstractSelectTag = Tag.valueOf(component.getClass().getSimpleName());
            Element abstractSelect = new Element(abstractSelectTag, "");

            Tag optionTag = Tag.valueOf("option");
            StubElement optionElement = new StubElement(optionTag, "");
            abstractSelect.appendChild(optionElement);

            try {
                component.readDesign(abstractSelect, new DesignContext());
            } catch (Exception ignore) {
            }

            return optionElement.searchItemId;
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
