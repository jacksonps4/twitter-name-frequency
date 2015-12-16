package com.minorityhobbies.tnf.core;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TweetUserFirstNameParserTest {
    private TweetUserFirstNameParser tweetUserFirstNameParser;

    @Before
    public void setUp() {
        tweetUserFirstNameParser = new TweetUserFirstNameParser();
    }

    @Test
    public void tweetUserName() {
        assertEquals("foo",
                tweetUserFirstNameParser.apply("{ \"user\": { \"name\": \"Foo Bar\", \"id_str\": \"123\" }}").getName());
    }

    @Test
    public void tweetUserId() {
        assertEquals("123",
                tweetUserFirstNameParser.apply("{ \"user\": { \"name\": \"Foo Bar\", \"id_str\": \"123\" }}").getId());
    }
}
