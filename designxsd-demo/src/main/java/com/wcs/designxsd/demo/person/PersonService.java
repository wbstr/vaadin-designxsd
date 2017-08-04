/*
 * Copyright 2017 lali.
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

import com.vaadin.data.provider.QuerySortOrder;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author lali
 */
public class PersonService {

    private final List<Person> data = Arrays.asList(
            new Person("Nicolaus", "Copernicus", 1543),
            new Person("Galileo", "Galilei", 1564),
            new Person("Johannes", "Kepler", 1571));

    public List<Person> fetchPersons(int offset, int limit, PersonFilter orElse, List<QuerySortOrder> sortOrders) {
        return data;
    }

    public int getPersonCount(PersonFilter filter) {
        return data.size();
    }
}
