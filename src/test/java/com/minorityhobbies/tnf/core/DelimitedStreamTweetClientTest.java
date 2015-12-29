package com.minorityhobbies.tnf.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DelimitedStreamTweetClientTest {
    private DelimitedStreamTweetClient delimitedStreamTweetClient;
    private List<String> tweets;

    @Before
    public void setUp() {
        tweets = new LinkedList<>();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        delimitedStreamTweetClient = new DelimitedStreamTweetClient(cl.getResource("tweet-stream.json").toExternalForm(),
                tweets::add, false, "token");
    }

    @After
    public void tearDown() {
        delimitedStreamTweetClient.close();
        tweets.clear();
    }

    @Test
    public void count() throws InterruptedException {
        Thread.sleep(200L);
        assertEquals(2, tweets.size());
    }
}
