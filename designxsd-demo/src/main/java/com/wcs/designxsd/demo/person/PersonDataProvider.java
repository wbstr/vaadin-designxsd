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

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import java.util.stream.Stream;

/**
 *
 * @author lali
 */
public class PersonDataProvider extends AbstractBackEndDataProvider<Person, PersonFilter> {

    @Override
    protected Stream<Person> fetchFromBackEnd(Query<Person, PersonFilter> query) {
        return new PersonService().fetchPersons(
                query.getOffset(),
                query.getLimit(),
                query.getFilter().orElse(null),
                query.getSortOrders()
        ).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<Person, PersonFilter> query) {
        return new PersonService().getPersonCount(
                query.getFilter().orElse(null)
        );
    }

}
