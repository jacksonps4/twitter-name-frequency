package com.minorityhobbies.tnf.core;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;

public class TweetUserFirstNameParser implements Function<String, String> {
    private final Set<String> exclusions = new HashSet<>();

    public TweetUserFirstNameParser() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("exclusions")))) {
            for (String line; (line = reader.readLine()) != null; ) {
                exclusions.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String apply(String tweetData) {
        JsonReader reader = Json.createReader(new StringReader(tweetData));
        JsonObject tweet = reader.readObject();
        if (tweet.containsKey("user")) {
            JsonObject user = tweet.getJsonObject("user");
            String name = user.getString("name");
            StringTokenizer st = new StringTokenizer(name);
            String value = null;
            if (st.hasMoreTokens()) {
                value = st.nextToken();
            }
            if (value != null && !exclusions.contains(value.toLowerCase()) && value.length() > 2
                    && value.matches("[[A-Z][a-z]]+")) {
                return value.toLowerCase();
            }
        }
        return null;
    }
}
