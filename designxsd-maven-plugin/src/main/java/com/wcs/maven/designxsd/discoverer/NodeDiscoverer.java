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
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 *
 * @author lali
 */
public class NodeDiscoverer {

    private boolean searchNode;

    public boolean discover(Class<? extends Component> componentClass) {
        Component component;
        try {
            component = componentClass.newInstance();
        } catch (Exception ex) {
            return false;
        }

        Tag componentTag = Tag.valueOf("mock-" + component.getClass().getSimpleName().toLowerCase());
        Element componentElement = new Element(componentTag, "");

        Tag nodeTag = Tag.valueOf("node");
        StubElement nodeElement = new StubElement(nodeTag, "");
        componentElement.appendChild(nodeElement);

        try {
            component.readDesign(componentElement, new DesignContext());
        } catch (Exception ex) {
            return false;
        }

        return searchNode;
    }

    private class StubElement extends Element {

        public StubElement(Tag tag, String baseUri, Attributes attributes) {
            super(tag, baseUri, attributes);
        }

        public StubElement(Tag tag, String baseUri) {
            super(tag, baseUri);
        }

        @Override
        public String attr(String attributeKey) {
            if (!searchNode) {
                // text attribute lesz az item-id
                searchNode = "text".equals(attributeKey);
            }
            return super.attr(attributeKey);
        }
    }
}
