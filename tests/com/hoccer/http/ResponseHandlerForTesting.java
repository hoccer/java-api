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
package com.hoccer.http;

import java.util.HashMap;

import com.hoccer.data.StreamableContent;

public class ResponseHandlerForTesting implements HttpResponseHandler {

    private static final String LOG_TAG                         = "ResponseHandlerForTesting";

    public boolean              areHeadersAvailable             = false;
    public boolean              hasOnHeadersAvailableBeenCalled = false;
    public boolean              hasError                        = false;
    public boolean              isReceiving                     = false;
    public boolean              wasSuccessful                   = false;

    public double               sendProgress                    = -1;
    public double               receiveProgress                 = -1;
    public StreamableContent    body                            = null;
    public int                  statusCode                      = -1;

    @Override
    public void onHeaderAvailable(HashMap<String, String> headers) {
        reset();
        areHeadersAvailable = true;
        hasOnHeadersAvailableBeenCalled = true;
    }

    @Override
    public void onError(int statusCode, StreamableContent body) {
        reset();
        hasError = true;
        this.statusCode = statusCode;
        this.body = body;
    }

    @Override
    public void onReceiving(double progress) {
        System.out.println("receiving: " + progress + "%");
        receiveProgress = progress;
    }

    @Override
    public void onSending(double progress) {
        System.out.println("sending: " + progress + "%");
        sendProgress = progress;
    }

    @Override
    public void onSuccess(int statusCode, StreamableContent body) {
        reset();
        this.statusCode = statusCode;
        wasSuccessful = true;
        this.body = body;
    }

    void reset() {
        areHeadersAvailable = hasError = isReceiving = wasSuccessful = false;
        body = null;
    }

    @Override
    public void onError(Exception e) {
        hasError = true;
    }

}
