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

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.TwinColSelect;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author lali
 */
public class OptionDiscovererTest {

    @Test
    public void testTable() {
        boolean useOptionTag = testOption(new Table());

        Assert.assertFalse(useOptionTag);
    }

    @Test
    public void testTree() {
        boolean useOptionTag = testOption(new Tree());

        Assert.assertFalse(useOptionTag);
    }

    @Test
    public void testNativeSelect() {
        boolean useOptionTag = testOption(new NativeSelect());

        Assert.assertTrue(useOptionTag);
    }

    @Test
    public void testSelect() {
        boolean useOptionTag = testOption(new Select());

        Assert.assertTrue(useOptionTag);
    }

    @Test
    public void testTwinColSelect() {
        boolean useOptionTag = testOption(new TwinColSelect());

        Assert.assertTrue(useOptionTag);
    }

    @Test
    public void testComboBox() {
        boolean useOptionTag = testOption(new ComboBox());

        Assert.assertTrue(useOptionTag);
    }

    @Test
    public void testListSelect() {
        boolean useOptionTag = testOption(new ListSelect());

        Assert.assertTrue(useOptionTag);
    }

    @Test
    public void testTreeTable() {
        boolean useOptionTag = testOption(new TreeTable());

        Assert.assertFalse(useOptionTag);
    }

    @Test
    public void testOptionGroup() {
        boolean useOptionTag = testOption(new OptionGroup());

        Assert.assertTrue(useOptionTag);
    }

    private boolean testOption(Component component) {
        OptionDiscoverer od = new OptionDiscoverer();
        return od.discover(component);
    }

}
