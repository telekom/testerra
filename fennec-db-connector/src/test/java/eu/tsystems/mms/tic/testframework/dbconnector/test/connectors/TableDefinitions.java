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

import eu.tsystems.mms.tic.testframework.dbconnector.Table;

/**
 * Class containing TableDefinitions for fennec DBConnector test.
 * 
 * Created with IntelliJ IDEA. User: pele Date: 18.05.12 Time: 13:41 *
 */
public class TableDefinitions extends Table {

    /** Instance of table TestTable. */
    public static final TableDefinitions TESTTABLE = new TestTable("testtable");
    /** Instance of LOBTable. */
    public static final TableDefinitions LOBTABLE = new LobTable("largeobjects");

    /**
     * Default constructor for a table.
     * 
     * @param table
     *            Name of the table.
     */
    public TableDefinitions(final String table) {
        super(table);
    }
}
