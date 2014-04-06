/*
 * Copyright 2014 Webstar Csoport Kft.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.wcs.maven.claraxsd;

/**
 *
 * @author kumm
 */
public final class NamingRules {

    public static final String CATALOG_FILENAME = "catalog.xml";

    private NamingRules() {
    }

    public static enum FixedName {

        BASE("clara_base.xsd", "clara://lib/base.xsd"),
        PARENT("clara_parent.xsd", "clara://lib/parent.xsd");

        private final String fileName;
        private final String SystemId;

        private FixedName(String fileName, String SystemId) {
            this.fileName = fileName;
            this.SystemId = SystemId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getSystemId() {
            return SystemId;
        }

    }

    public static String getGeneratedXsdFileName(Package componentPackage) {
        return componentPackage.getName() + ".xsd";
    }

    public static String getGeneratedXsdSystemId(Package componentPackage) {
        return "clara://" + componentPackage.getName() + ".xsd";
    }

}
