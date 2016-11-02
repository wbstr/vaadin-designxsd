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
package com.wcs.maven.designxsd.packagemapping;

import com.vaadin.ui.declarative.DesignContext;
import java.io.IOException;
import java.io.InputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

/**
 *
 * @author lali
 */
class PackageDesignContext extends DesignContext {

    private static final String UTF8 = "UTF-8";

    PackageDesignContext(InputStream designFile) throws PackageMappingsReaderException {
        Document doc;
        try {
            doc = Jsoup.parse(designFile, UTF8, "", Parser.htmlParser());
        } catch (IOException ex) {
            throw new PackageMappingsReaderException("The html document cannot be parsed.", ex);
        }

        super.readPackageMappings(doc);
    }

}
