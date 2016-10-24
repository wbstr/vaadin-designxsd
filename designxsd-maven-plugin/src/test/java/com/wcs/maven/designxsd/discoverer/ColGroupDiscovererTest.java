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

import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author lali
 */
public class ColGroupDiscovererTest {

    @Test
    public void testTable() {
        ColGroupDiscoverer discoverer = new ColGroupDiscoverer();
        boolean hasColGroup = discoverer.discover(new Table());

        assertTrue(hasColGroup);
    }

    @Test
    public void testTreeTable() {
        ColGroupDiscoverer discoverer = new ColGroupDiscoverer();
        boolean hasColGroup = discoverer.discover(new TreeTable());

        assertTrue(hasColGroup);
    }

    @Test
    public void testGrid() {
        ColGroupDiscoverer discoverer = new ColGroupDiscoverer();
        boolean hasColGroup = discoverer.discover(new Grid());

        assertTrue(hasColGroup);
    }

    @Test
    public void testHorizontalLayout() {
        ColGroupDiscoverer discoverer = new ColGroupDiscoverer();
        boolean hasColGroup = discoverer.discover(new HorizontalLayout());

        assertFalse(hasColGroup);
    }
}
