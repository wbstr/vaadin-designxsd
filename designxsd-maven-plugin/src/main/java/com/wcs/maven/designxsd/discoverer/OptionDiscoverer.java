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

import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.Component;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {

    public boolean discover(Class<? extends Component> componentClass) {
        switch (componentClass.getName()) {
            // v7
            case "com.vaadin.v7.ui.NativeSelect":
            case "com.vaadin.v7.ui.Select":
            case "com.vaadin.v7.ui.TwinColSelect":
            case "com.vaadin.v7.ui.ComboBox":
            case "com.vaadin.v7.ui.ListSelect":
            case "com.vaadin.v7.ui.OptionGroup":
            // v8
            case "com.vaadin.ui.RadioButtonGroup":
            case "com.vaadin.ui.NativeSelect":
            case "com.vaadin.ui.TwinColSelect":
            case "com.vaadin.ui.CheckBoxGroup":
            case "com.vaadin.ui.ComboBox":
            case "com.vaadin.ui.ListSelect":
                return true;
        }

        return false;
    }
}
