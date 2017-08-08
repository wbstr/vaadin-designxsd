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
package com.wcs.designxsd.demo.person;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.wcs.designxsd.xdesign.XDesign;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lali
 */
@DesignRoot
public class PersonComponent extends VerticalLayout {

    private static final Map<String, String> CAPTION_STORE;

    static {
        CAPTION_STORE = new HashMap<>();
        CAPTION_STORE.put("com.wcs.designxsd.demo.person.Person.lastName", "Last name");
        CAPTION_STORE.put("com.wcs.designxsd.demo.person.Person.firstName", "First name");
        CAPTION_STORE.put("com.wcs.designxsd.demo.person.Person.birth", "Birth");
    }

    Grid<Person> xmlGrid;

    public PersonComponent() {
        XDesign.readAndConfigurate(this, (model, propertyName) -> {
            String caption = CAPTION_STORE.get(model.getName() + "." + propertyName);
            return caption != null ? caption : "???";
        });
    }

}
