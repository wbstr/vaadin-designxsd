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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

/**
 *
 * @author lali
 */
public class ColGroupDiscoverer {
    
    private static final Logger LOGGER = Logger.getLogger(ColGroupDiscoverer.class.getName());
    
    private boolean searchColGroup;

    public boolean discover(Component component) {
        Tag componentTag = Tag.valueOf("v-" + component.getClass().getSimpleName());
        StubElement componentElement = new StubElement(componentTag, "");

        Tag tableTag = Tag.valueOf("table");
        StubElement tableElement = new StubElement(tableTag, "");
        componentElement.appendChild(tableElement);

        Tag colgroupTag = Tag.valueOf("colgroup");
        StubElement colgroupElement = new StubElement(colgroupTag, "");
        tableElement.appendChild(colgroupElement);

        Tag colTag = Tag.valueOf("col");
        StubElement colElement = new StubElement(colTag, "");
        colgroupElement.appendChild(colElement);

        try {
            component.readDesign(componentElement, new DesignContext());
        } catch (Exception ex) {
            String msg = "Colgroup search skipped. Can not read component."
                    + "Component name: " + component.getClass().getName();
            LOGGER.log(Level.WARNING, msg, ex);
            return false;
        }

        return searchColGroup;
    }

    private class StubElement extends Element {

        public StubElement(Tag tag, String baseUri, Attributes attributes) {
            super(tag, baseUri, attributes);
        }

        public StubElement(Tag tag, String baseUri) {
            super(tag, baseUri);
        }

        @Override
        public Elements getElementsByTag(String tagName) {
            if (!searchColGroup) {
                searchColGroup = "colgroup".equals(tagName);
            }
            return super.getElementsByTag(tagName);
        }

        @Override
        public Elements select(String cssQuery) {
            if (!searchColGroup) {
                searchColGroup = cssQuery.contains("colgroup");
            }
            return super.select(cssQuery);
        }
    }
}
