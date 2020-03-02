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
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.dbconnector;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Class representing a DB table. Tables its and subclasses are used in Query objects to build queries.
 *
 * Parameterizing the generic type Query with a table will result in restricting queries only to the named table class
 * or their subclasses.
 *
 * In addition columns can be declared as public string properties of the class. They are automatically instantiated
 * with their name as value. These column fields can then be used at building queries too.
 *
 * For samples see the containing class MyTable.
 *
 * @author sepr
 */
public abstract class Table {

    /** Name of the table in the DB */
    private final String tableName;

    /**
     * Constructor creating a table object.
     *
     * @param table Name of the table in the DB.
     */
    protected Table(final String table) {
        this.tableName = table;
        setFieldValues();
    }

    /**
     * gets the name of the table
     *
     * @return Name of DB Table.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Fills all declared Fields of the type String in this class with their names as values.
     * <code>public static String column1;</code> will have the value "column1".
     */
    private void setFieldValues() {
        final Field[] fields = this.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                if (field.getType() == String.class && field.get(this) == null) {
                    field.set(this, field.getName());
                }
            } catch (final Exception e) {
                LoggerFactory.getLogger(this.getClass()).trace(field.getName() + ": " + e.toString());
            }
        }
    }
}

/**
 * Sample Table declaration.
 *
 * @author sepr
 */
class MyTable extends Table {

    /** Static accessor for the Table. */
    public static final MyTable THETABLE = new MyTable("my.project.table");

    /**
     * Field declaring a column "firstname". When using this field the value firstname is automatically used as column
     * name.
     */
    private static String firstname;
    /**
     * Column fields can also be instantiated manually. This can be important when a column name changes. Queries using
     * the field don't need to be changed.
     */
    private static String lastname = "surname";

    /**
     * Default constructor implementation.
     *
     * @param table Tablename.
     */
    public MyTable(final String table) {
        super(table);
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
}
