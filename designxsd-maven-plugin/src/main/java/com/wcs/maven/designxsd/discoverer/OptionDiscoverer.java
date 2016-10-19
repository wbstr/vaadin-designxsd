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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {

    private static final Set<String> OPTION_COMPONENT_NAMES = new HashSet<String>() {
        {
            add("com.vaadin.ui.OptionGroup");
            add("com.vaadin.ui.ListSelect");
            add("com.vaadin.ui.ComboBox");
            add("com.vaadin.ui.TwinColSelect");
            add("com.vaadin.ui.Select");
            add("com.vaadin.ui.NativeSelect");
        }
    };

    public boolean discover(Component component) {
        return OPTION_COMPONENT_NAMES.contains(component.getClass().getName());
    }

}
