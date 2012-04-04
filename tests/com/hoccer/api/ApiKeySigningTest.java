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
package com.hoccer.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ApiKeySigningTest {

    public final static String demoKey    = "e101e890ea97012d6b6f00163e001ab0";
    public final static String demoSecret = "JofbFD6w6xtNYdaDgp4KOXf/k/s=";

    @Test
    public void digestingTest() throws Exception {

        String text = "http://eight.local:9292/?expires_in=23&api_key=115745c0d609012d2f4e001ec2be2ed9&timestamp=1290179818";
        String signature = ApiSigningTools.digest(text, "DNonxFIWCxS3kHgC9oVG+lUz/60=");

        assertThat(signature, is(equalTo("I8rij7X0vKohwFuChCLdtS4IMM8=")));
    }
}
