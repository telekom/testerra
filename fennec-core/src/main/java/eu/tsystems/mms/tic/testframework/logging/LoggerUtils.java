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
 * Created on 23.04.2012
 * 
 * Copyright(c) 2011 - 2011 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.logging;

import org.slf4j.Logger;

/**
 * Utility class for logging methods.
 * 
 * @author pele
 */
public final class LoggerUtils {

    /**
     * Private constructor to hide the public one since this a static only class.
     */
    private LoggerUtils() {
    }

    /**
     * Configure the log level for fennec.
     * 
     * @param logger The logger to log.
     * @param level The LogLevel which log messages should be logged,
     * @param msg The message to log.
     */
    public static void logWithLogLevel(final Logger logger, final LogLevel level, final String msg) {
        switch (level) {
        case ERROR:
            logger.error(msg);
            break;
        case WARNING:
            logger.warn(msg);
            break;
        case INFO:
            logger.info(msg);
            break;
        case DEBUG:
            logger.debug(msg);
            break;
        default:
            logger.info(msg);
            break;
        }
    }
}
