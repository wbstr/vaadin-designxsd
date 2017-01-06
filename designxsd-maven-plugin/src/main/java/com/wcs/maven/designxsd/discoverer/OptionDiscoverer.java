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
import com.vaadin.ui.TwinColSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Select;

/**
 *
 * @author lali
 */
public class OptionDiscoverer {
    
    private boolean searchItemId;
    
    public boolean discover(Class<? extends Component> componentClass) {
        String componentName = componentClass.getName();
        return NativeSelect.class.getName().equals(componentName)
                || Select.class.getName().equals(componentName)
                || TwinColSelect.class.getName().equals(componentName)
                || ComboBox.class.getName().equals(componentName)
                || ListSelect.class.getName().equals(componentName)
                || OptionGroup.class.getName().equals(componentName);
    }
}
