/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
