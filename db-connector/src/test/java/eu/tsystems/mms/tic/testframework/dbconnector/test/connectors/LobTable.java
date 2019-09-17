/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 13.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.dbconnector.test.connectors;

/**
 * Class containing structure of largeobjects table in db Testerradbconnector.
 * 
 * @author sepr
 * 
 */
public class LobTable extends TableDefinitions {

    /** table column blob. */
    private static String blob;
    /** table column clob. */
    private static String clob;
    /** table column id. */
    private static String idField = "id";
    /** table column blobType. */
    private static String blobType;

    /**
     * Constructor for this table.
     * 
     * @param table Name of the table.
     */
    public LobTable(final String table) {
        super(table);
    }

    /**
     * sets the blob
     *
     * @param blob the blob to set
     */
    public static void setBlob(final String blob) {
        LobTable.blob = blob;
    }

    /**
     * sets the clob
     *
     * @param clob the clob to set
     */
    public static void setClob(final String clob) {
        LobTable.clob = clob;
    }

    /**
     * sets the id field
     *
     * @param idField the idField to set
     */
    public static void setIdField(final String idField) {
        LobTable.idField = idField;
    }

    /**
     * sets the blob type
     *
     * @param blobType the blobType to set
     */
    public static void setBlobType(final String blobType) {
        LobTable.blobType = blobType;
    }

    /**
     * gets the blob
     *
     * @return the blob
     */
    public static String getBlob() {
        return blob;
    }

    /**
     * gets the clob
     *
     * @return the clob
     */
    public static String getClob() {
        return clob;
    }

    /**
     * gets the id field
     *
     * @return the idField
     */
    public static String getIdField() {
        return idField;
    }

    /**
     * gets the blob type
     *
     * @return the blobType
     */
    public static String getBlobType() {
        return blobType;
    }
}
