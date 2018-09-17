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
 * Table skeleton of mysql test table.
 * 
 * @author sepr
 * 
 */
public class TestTable extends TableDefinitions {

    /** Column id. */
    private static String idField = "id";
    /** Column user. */
    private static String user = "userName";
    /** Column firstname. */
    private static String firstname = "firstname";
    /** Column lastname. */
    private static String lastname = "where";
    /** Column age. */
    private static String age = "age";
    /** Column date. */
    private static String date = "date";
    /** Column creationTime. */
    private static String creationTime = "creationTime";

    /**
     * Constructor of the table.
     * 
     * @param table name of the table.
     */
    public TestTable(final String table) {
        super(table);
    }

    /**
     * sets the id field from test table
     *
     * @param idField the idField to set
     */
    public static void setIdField(final String idField) {
        TestTable.idField = idField;
    }

    /**
     * sets the user from test table
     *
     * @param user the user to set
     */
    public static void setUser(final String user) {
        TestTable.user = user;
    }

    /**
     * sets the firstname from test table
     *
     * @param firstname the firstname to set
     */
    public static void setFirstname(final String firstname) {
        TestTable.firstname = firstname;
    }

    /**
     * sets the lastname from test table
     *
     * @param lastname the lastname to set
     */
    public static void setLastname(final String lastname) {
        TestTable.lastname = lastname;
    }

    /**
     * sets the age from test table
     *
     * @param age the age to set
     */
    public static void setAge(final String age) {
        TestTable.age = age;
    }

    /**
     * sets the date from test table
     *
     * @param date the date to set
     */
    public static void setDate(final String date) {
        TestTable.date = date;
    }

    /**
     * sets creation time from test table
     *
     * @param creationTime the creationTime to set
     */
    public static void setCreationTime(final String creationTime) {
        TestTable.creationTime = creationTime;
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
     * gets the user
     *
     * @return the user
     */
    public static String getUser() {
        return user;
    }

    /**
     * gets the firstname
     *
     * @return the firstname
     */
    public static String getFirstname() {
        return firstname;
    }

    /**
     * gets the lastname
     *
     * @return the lastname
     */
    public static String getLastname() {
        return lastname;
    }

    /**
     * gets the age
     *
     * @return the age
     */
    public static String getAge() {
        return age;
    }

    /**
     * gets the date
     *
     * @return the date
     */
    public static String getDate() {
        return date;
    }

    /**
     * gets the creation time
     *
     * @return the creationTime
     */
    public static String getCreationTime() {
        return creationTime;
    }
}
