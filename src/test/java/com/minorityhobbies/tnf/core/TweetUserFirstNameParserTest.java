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
        assertEquals("Foo", tweetUserFirstNameParser.apply("{ \"user\": { \"name\": \"Foo Bar\" }}"));
    }
}
