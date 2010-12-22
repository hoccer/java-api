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

import java.io.IOException;

import org.apache.http.HttpResponse;

public abstract class HttpException extends Exception {

    // Static Methods ----------------------------------------------------

    /**
     * 
     */
    private static final long serialVersionUID = -7901299732283491664L;

    public static void throwIfError(String pUrl, HttpResponse pResponse)
            throws HttpServerException, HttpClientException, IOException {

        int status = pResponse.getStatusLine().getStatusCode();
        if (status >= 400 && status < 500) {
            throw new HttpClientException(pUrl, pResponse);
        }
        if (status >= 500 && status < 600) {
            throw new HttpServerException(pUrl, pResponse);
        }
    }

    private static String getServerMessageOf(HttpResponse pResponse) throws IOException {

        String message = " -- response is: \n";

        if (pResponse.getEntity() == null) {
            return message + "<empty>";
        }
        if (pResponse.getStatusLine().getStatusCode() == 404) {
            // server message is not interesting when 404
            return " 'Not Found'";
        }

        message += HttpHelper.extractBodyAsString(pResponse.getEntity());
        return message.substring(0, Math.min(500, message.length() - 1));
    }

    // Instance Variables ------------------------------------------------

    private final int              mStatusCode;

    /**
     * HttpResponse objects are not serializable, thus the response is transient
     */
    private transient HttpResponse mResponse;

    public HttpException(String pUrl, int pStatusCode) {

        super("HTTP request for '" + pUrl + "'failed with status code " + pStatusCode
                + " (response is not available).");
        mStatusCode = pStatusCode;
    }

    public HttpException(String pUrl, HttpResponse pResponse) throws IOException {

        super("HTTP request for '" + pUrl + "' failed with status code "
                + pResponse.getStatusLine().getStatusCode() + getServerMessageOf(pResponse));
        mStatusCode = pResponse.getStatusLine().getStatusCode();
        mResponse = pResponse;
    }

    // Public Instance Methods -------------------------------------------

    public int getStatusCode() {

        return mStatusCode;
    }

    /**
     * @return the HTTP response, if available -- null otherwise
     */
    public HttpResponse getHttpResponse() {

        return mResponse;
    }

    public boolean hasHttpResponse() {

        return mResponse != null;
    }
}
