/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com> These coded instructions,
 * statements, and computer programs contain proprietary information of Hoccer GmbH Berlin, and are
 * copy protected by law. They may be used, modified and redistributed under the terms of GNU
 * General Public License referenced below. Alternative licensing without the obligations of the GPL
 * is available upon request. GPL v3 Licensing: This file is part of the "Linccer Java-API". Linccer
 * Java-API is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Linccer Java-API is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with Linccer Java-API. If
 * not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.util;

import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to manage properties of all loggers used by Hoccer (and only
 * these).
 * 
 * @author Arne Handt, it@handtwerk.de
 * 
 */
public class HoccerLoggers {

	// Constants ---------------------------------------------------------

	private static final String LOG_TAG = HoccerLoggers.class.getSimpleName();
	private static final Logger LOG = Logger.getLogger(LOG_TAG);

	private static final HashSet<String> sUsedLoggerNames = new HashSet<String>();

	private static final HashSet<Handler> sHandlers = new HashSet<Handler>();

	// Static Methods ----------------------------------------------------

	/**
	 * Get logger for a particular name. Makes sure that the logger uses the
	 * handlers that should be used by Hoccer loggers.
	 * 
	 * @param pName
	 * @return
	 */
	public static Logger getLogger(String pName) {

		LOG.info("getLogger " + pName);

		Logger logger = Logger.getLogger(pName);

		synchronized (sUsedLoggerNames) {

			if (!sUsedLoggerNames.contains(pName)) {

				sUsedLoggerNames.add(pName);
			}
		}

		updateHandlers(logger);
		logger.setLevel(Level.ALL);

		return logger;
	}

	/**
	 * Get logger for a particular class.
	 *
	 * Convenience wrapper around getLogger(String).
	 *
	 * @param pClass
	 * @return
	 */
	public static Logger getLogger(Class<?> pClass) {
		return getLogger(pClass.getSimpleName());
	}

	/**
	 * Add a handler globally to all the loggers used by Hoccer.
	 * 
	 * @param pHandler
	 */
	public static void addHandler(Handler pHandler) {

		LOG.info("addHandler " + pHandler);

		synchronized (sHandlers) {

			sHandlers.add(pHandler);
		}

		synchronized (sUsedLoggerNames) {

			for (String loggerName : sUsedLoggerNames) {

				Logger logger = Logger.getLogger(loggerName);
				updateHandlers(logger);
			}
		}
	}

	/**
	 * Make sure the given logger uses all the handlers defined for this group
	 * of loggers.
	 * 
	 * @param pLogger
	 */
	private static void updateHandlers(Logger pLogger) {

		synchronized (pLogger) {

			HashSet<Handler> loggerHandlers = new HashSet<Handler>();
			for (Handler handler : pLogger.getHandlers()) {

				loggerHandlers.add(handler);
			}

			HashSet<Handler> newHandlers;
			synchronized (sHandlers) {

				newHandlers = new HashSet<Handler>(sHandlers);
			}

			newHandlers.removeAll(loggerHandlers);

			for (Handler newHandler : newHandlers) {

				pLogger.addHandler(newHandler);
			}
		}
	}
}
