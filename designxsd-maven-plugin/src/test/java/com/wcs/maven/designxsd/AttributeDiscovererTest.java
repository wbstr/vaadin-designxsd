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
package com.wcs.maven.designxsd;

import com.wcs.maven.designxsd.discoverer.AttributeDiscoverer;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Component;
import com.vaadin.ui.Flash;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import org.junit.Test;

/**
 *
 * @author lali
 */
public class AttributeDiscovererTest {

    @Test
    public void testVerticalLayout() {
        Component c = new VerticalLayout();
        discovery(c);
    }

    @Test
    public void testFlash() {
        Component c = new Flash();
        discovery(c);
    }

    @Test
    public void testVerticalSplitPanel() {
        Component c = new VerticalSplitPanel();
        discovery(c);
    }

    @Test
    public void testTreeTable() {
        Component c = new TreeTable();
        discovery(c);
    }

    @Test
    public void testVideo() {
        Component c = new Video();
        discovery(c);
    }

    @Test
    public void testTable() {
        Component c = new Table();
        discovery(c);
    }

    @Test
    public void testColorPicker() {
        Component c = new ColorPicker();
        discovery(c);
    }

    @Test
    public void testColorWindow() {
        Component c = new Window();
        discovery(c);
    }

    private void discovery(Component c) {
        System.out.println(new AttributeDiscoverer().discovery(c));
    }
}
