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
package eu.tsystems.mms.tic.testframework.dbconnector.test.connectors;

import java.sql.Time;
import java.util.Date;

/**
 * Java object to hold entries of TestTable.
 *
 * @author sepr
 *
 */
public class DBEntry {

    /** Age of user. */
    private final int age;
    /** Column user. */
    private final String user;
    /** Column firstname. */
    private final String firstname;
    /** Column lastname. */
    private final String lastname;
    /** Date of db entry. */
    private final Date date;
    /** Time of DB entry. */
    private final Time creationTime;

    /**
     * Creates a db entry object with all fields.
     *
     * @param user userName.
     * @param first firstName.
     * @param last lastName
     * @param age Age.
     * @param date Date of db entry.
     * @param time Time of db entry.
     */
    public DBEntry(final String user, final String first, final String last, final int age, final Date date,
            final Time time) {
        this.age = age;
        this.creationTime = time;
        this.date = date;
        this.firstname = first;
        this.lastname = last;
        this.user = user;
    }

    /**
     * gets the age
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * gets the user
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * gets the first name
     *
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * gets the last name
     *
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * gets the date
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * gets the creation time
     *
     * @return the creationTime
     */
    public Time getCreationTime() {
        return creationTime;
    }
}
