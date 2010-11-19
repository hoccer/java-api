package com.hoccer.api;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class TestCloudServiceInternals extends CloudService {

    public TestCloudServiceInternals() {
        super(new ClientDescription("unit test"));
    }

    @Test
    public void digestingTest() throws Exception {

        String text = "http://eight.local:9292/?expires_in=23&api_key=115745c0d609012d2f4e001ec2be2ed9&timestamp=1290179818";
        String signature = digest(text, "DNonxFIWCxS3kHgC9oVG+lUz/60=");

        assertThat(signature, is(equalTo("I8rij7X0vKohwFuChCLdtS4IMM8=")));
    }
}
