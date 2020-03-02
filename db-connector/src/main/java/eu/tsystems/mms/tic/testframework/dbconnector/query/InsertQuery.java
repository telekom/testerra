/*
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
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.dbconnector.query;

import eu.tsystems.mms.tic.testframework.dbconnector.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class representing insert queries.
 *
 * @param <T>
 *            Table to insert data into.
 *
 * @author sepr
 */
public class InsertQuery<T extends Table> extends Query<T> implements INonSelectQuery {

    /**
     * Method to create an INSERT SQL query statement.
     *
     * @param intoTable
     *            Table to insert data into.
     * @param cols
     *            Columns to insert data into. If null values for all Columns must be stated.
     * @param values
     *            Values to insert.
     */
    public InsertQuery(final T intoTable, final String[] cols, final String[] values) {
        this.setFromTable(intoTable);
        String c = String.join(",", cols);
        String v = Arrays.stream(values).map(s -> "'" + s + "'").collect(Collectors.joining(","));
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", intoTable.getTableName(), c, v);
        setQueryString(query);
    }
}
