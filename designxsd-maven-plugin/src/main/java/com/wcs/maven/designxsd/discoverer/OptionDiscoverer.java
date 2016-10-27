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
import com.vaadin.ui.declarative.DesignContext;
import java.util.logging.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {

    private static final Logger LOGGER = Logger.getLogger(OptionDiscoverer.class.getName());

    private boolean searchItemId;

    public boolean discover(Component c) {
        Tag abstractSelectTag = Tag.valueOf(c.getClass().getSimpleName());
        Element abstractSelect = new Element(abstractSelectTag, "");

        Tag optionTag = Tag.valueOf("option");
        StubElement optionElement = new StubElement(optionTag, "");
        abstractSelect.appendChild(optionElement);

        try {
            c.readDesign(abstractSelect, new DesignContext());

        } catch (Exception ex) {
            return false;
        }

        return searchItemId;
    }

    private class StubElement extends Element {

        public StubElement(Tag tag, String baseUri, Attributes attributes) {
            super(tag, baseUri, attributes);
        }

        public StubElement(Tag tag, String baseUri) {
            super(tag, baseUri);
        }

        @Override
        public boolean hasAttr(String attributeKey) {
            if (!searchItemId) {
                searchItemId = "item-id".equals(attributeKey);
            }
            return super.hasAttr(attributeKey);
        }
    }
}
