/*******************************************************************************
 * Copyright (C) 2009, 2010, Hoccer GmbH Berlin, Germany <www.hoccer.com>
 * 
 * These coded instructions, statements, and computer programs contain
 * proprietary information of Hoccer GmbH Berlin, and are copy protected
 * by law. They may be used, modified and redistributed under the terms
 * of GNU General Public License referenced below. 
 *    
 * Alternative licensing without the obligations of the GPL is
 * available upon request.
 * 
 * GPL v3 Licensing:
 * 
 * This file is part of the "Linccer Java-API".
 * 
 * Linccer Java-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Linccer Java-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Linccer Java-API. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.hoccer.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hoccer.http.AsyncHttpGet;

public class TestHelper {

    static final String LOG_TAG = "TestHelper";

    public static void blockUntilTrue(String pFailMessage, long pTimeout, TestHelper.Condition pCon)
            throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < pTimeout) {

            if (pCon.isSatisfied()) {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError(pFailMessage);
    }

    public static void blockUntilFalse(String pFailMessage, long pTimeout,
            final TestHelper.Condition pCon) throws Exception {
        blockUntilTrue(pFailMessage, pTimeout, new TestHelper.Condition() {
            public boolean isSatisfied() throws Exception {

                return !pCon.isSatisfied();
            }
        });
    }

    public static void blockUntilNull(String pFailMessage, long pTimeout,
            final TestHelper.Measurement pMeasurement) throws Exception {

        blockUntilEquals(pFailMessage, pTimeout, null, pMeasurement);
    }

    public static void blockUntilNotNull(String pFailMessage, long pTimeout,
            final TestHelper.Measurement pMeasurement) throws Exception {
        blockUntilTrue(pFailMessage, pTimeout, new Condition() {
            @Override
            public boolean isSatisfied() throws Exception {
                return pMeasurement.getActualValue() != null;
            }
        });
    }

    public static void blockUntilEquals(String pFailMessage, long pTimeout, Object pExpected,
            final TestHelper.Measurement pMesurement) throws Exception {
        Object mesuredValue = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < pTimeout) {
            mesuredValue = pMesurement.getActualValue();
            if (pExpected.equals(mesuredValue)) {
                return;
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }

        throw new AssertionFailedError(pFailMessage + ": should be <" + pExpected + ">, but was <"
                + mesuredValue + ">");
    }

    public static void blockUntilResourceAvailable(String pFailMessage, final String pUrl)
            throws Exception {
        blockUntilResourceAvailable(pFailMessage, pUrl, 3000);
    }

    public static void blockUntilResourceAvailable(String pFailMessage, final String pUrl,
            int pTimeout) throws Exception {
        final HttpClient client = new DefaultHttpClient();

        blockUntilTrue(pFailMessage, pTimeout, new TestHelper.Condition() {
            @Override
            public boolean isSatisfied() {
                HttpGet request = new HttpGet(pUrl);
                try {
                    HttpResponse response = client.execute(request);
                    int statuscode = response.getStatusLine().getStatusCode();
                    if (statuscode == 200 || statuscode == 201)
                        return true;
                    else {
                        Thread.sleep(100);
                        return false;
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public interface Condition {
        public boolean isSatisfied() throws Exception;
    }

    /**
     * Mesures current state of an object.
     */
    public interface Measurement {
        public Object getActualValue() throws Exception;
    }

    /**
     * Assert two {@linkplain File files} to have equal content.
     * 
     * @param message
     *            the error message
     * @param expected
     *            reference file
     * @param current
     *            file to compare
     * @author Apache Project (package org.apache.commons.id.test)
     * @license Apache Lichense 2.0
     */
    public static void assertFileEquals(final String message, final File expected,
            final File current) {
        try {
            assertInputStreamEquals(new BufferedInputStream(new FileInputStream(expected)),
                    new BufferedInputStream(new FileInputStream(current)));
        } catch (final FileNotFoundException e) {
            Assert.fail((message != null ? message + ": " : "") + e.getMessage());
        }
    }

    private static void assertInputStreamEquals(final InputStream expected,
            final InputStream current) {
        assertInputStreamEquals(null, expected, current);
    }

    /**
     * Assert two {@linkplain InputStream input streams} to deliver equal content.
     * 
     * @param message
     *            the error message
     * @param expected
     *            reference input
     * @param current
     *            input to compare
     * @since 1.0
     * @author Apache Project (package org.apache.commons.id.test)
     * @license Apache Lichense 2.0
     */
    public static void assertInputStreamEquals(final String message, final InputStream expected,
            final InputStream current) {
        long counter = 0;
        int eByte, cByte;
        try {
            for (; (eByte = expected.read()) != -1; ++counter) {
                cByte = current.read();
                if (eByte != cByte) {
                    Assert.assertEquals((message != null ? message + ": " : "")
                            + "Stream not equal at position " + counter, eByte, cByte);
                }
            }
        } catch (final IOException e) {
            Assert.fail((message != null ? message + ": " : "") + e.getMessage());
        }
    }

    public static void assertGreater(String message, double minimum, double measured) {
        if (minimum > measured) {
            Assert.fail(message + " but " + minimum + " is greater than " + measured);
        }
    }

    public static void assertGreater(String message, int minimum, int measured) {
        if (minimum > measured) {
            Assert.fail(message + " but " + minimum + " is greater than " + measured);
        }
    }

    public static void assertSmaller(String message, double maximum, double measured) {
        if (maximum < measured) {
            Assert.fail(message + " but " + maximum + " is smaller than " + measured);
        }
    }

    public static void assertSmaller(String message, int maximum, int measured) {
        if (maximum < measured) {
            Assert.fail(message + " but " + maximum + " is smaller than " + measured);
        }
    }

    public static void assertIncludes(String message, String substring, String measured) {
        if (!measured.contains(substring)) {
            Assert.fail(message + " but '" + measured + "' does not contain '" + substring + "'");
        }
    }

    public static void assertMatches(String message, String regexp, String measured) {
        if (!measured.matches(regexp)) {
            Assert.fail(message + " but '" + regexp + "' does not match '" + measured + "'");
        }
    }

    public static void assertEquals(final String message, final byte[] expected,
            final byte[] current) {

        int eByte, cByte;
        int i = 0;
        try {
            for (; i < expected.length; ++i) {
                eByte = expected[i];
                cByte = current[i];

                if (eByte != cByte) {
                    Assert.assertEquals((message != null ? message + ": " : "")
                            + "Byte Array not equal at position " + i, eByte, cByte);
                }
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
            Assert.fail((message != null ? message + ": " : "")
                    + "Byte Array truncated at position  " + i);
        }

        Assert.assertEquals("byte array to long", expected.length, current.length);

    }

    public static File[] getFiles(String dirName) throws Exception {
        return getFiles(dirName, "");
    }

    public static File[] getFiles(String dirName, final String pFileEnding) throws Exception {
        File dataDir = new File(dirName);
        Assert.assertTrue("dir should exists", dataDir.exists());
        Assert.assertTrue(dirName + " should be a directory", dataDir.isDirectory());
        Assert.assertTrue("dir should be readable", dataDir.canRead());

        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(pFileEnding);
            }
        };

        return dataDir.listFiles(filter);
    }

    public static void assertRequestIsDone(final AsyncHttpGet request) throws Exception {
        TestHelper.blockUntilTrue("should finish", 3000, new TestHelper.Condition() {

            @Override
            public boolean isSatisfied() throws Exception {
                return request.isTaskCompleted();
            }
        });
    }
}
