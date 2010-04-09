package com.artcom.y60.http;

import java.io.ByteArrayInputStream;

import com.artcom.y60.TestHelper;

public class TestAsyncPut extends HttpTestCase {
    
    private AsyncHttpPut mRequest;
    
    public void testExecution() throws Exception {
        
        getServer().setResponseDelay(200);
        mRequest = new AsyncHttpPut(getServer().getUri());
        mRequest.start();
        
        TestHelper.blockUntilTrue("request should have started by now", 1000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 0;
                    }
                });
        assertTrue("request should have started, but progress is " + mRequest.getProgress() + "%",
                mRequest.isRunning());
        
        TestHelper.blockUntilTrue("request should have got the response by now", 4000,
                new TestHelper.Condition() {
                    @Override
                    public boolean isSatisfied() throws Exception {
                        return mRequest.getProgress() > 1;
                    }
                });
        
        assertRequestIsDone(mRequest);
    }
    
    public void testPuttingStringData() throws Exception {
        String uri = getServer().getUri() + "/data";
        mRequest = new AsyncHttpPut(uri);
        mRequest.setBody("my data string");
        mRequest.start();
        assertRequestIsDone(mRequest);
        assertEquals("the putted data should be returned as answer from the server",
                "my data string", mRequest.getBodyAsString());
        assertEquals("the putted data should be getrievable via http GET", "my data string",
                HttpHelper.getAsString(uri));
    }
    
    public void testPuttingMultipart() throws Exception {
        String uri = getServer().getUri() + "/myMultipart";
        MultipartHttpEntity multipart = new MultipartHttpEntity();
        byte[] bytes = ("test data string as stream").getBytes();
        multipart.addPart("unit test data", "afilename.txt", "text/plain", bytes.length,
                new ByteArrayInputStream(bytes));
        
        mRequest = new AsyncHttpPut(uri);
        mRequest.setBody(multipart);
        mRequest.start();
        
        assertRequestIsDone(mRequest);
        TestHelper.assertIncludes(
                "the putted data should be somewhere in the returned as answer from the server",
                "test data string as stream", mRequest.getBodyAsString());
        
        String dataString = HttpHelper.getAsString(uri);
        assertTrue("putted data should contain mulitpart border string", dataString
                .contains(MultipartHttpEntity.BORDER));
        assertTrue("putted data should contain content-type informations", dataString
                .contains("Content-Type: text/plain"));
        assertTrue("putted data should contain transfer encoding", dataString
                .contains("Content-Transfer-Encoding: binary"));
        
        int posOfEmptyLine = dataString.indexOf("\r\n\r\n");
        assertTrue("should find the empty line in " + posOfEmptyLine, posOfEmptyLine != -1);
        int posOfMultipartEnd = dataString.indexOf("\r\n--" + MultipartHttpEntity.BORDER + "--");
        assertTrue("should find the multipart end in " + dataString, posOfMultipartEnd != -1);
        assertEquals("the putted data should be placed inside the multipart data!",
                "test data string as stream", dataString.substring(posOfEmptyLine + 4,
                        posOfMultipartEnd));
    }
}
