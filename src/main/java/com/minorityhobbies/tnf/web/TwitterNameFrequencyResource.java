package com.minorityhobbies.tnf.web;

import com.minorityhobbies.tnf.api.NameFrequency;
import com.minorityhobbies.tnf.services.TwitterAccountNameFrequencyDetector;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Path("names")
public class TwitterNameFrequencyResource {
    @Inject
    TwitterAccountNameFrequencyDetector twitterAccountNameFrequencyDetector;

    @GET
    @Path("names.json")
    public List<NameFrequency> frequencies() {
        return twitterAccountNameFrequencyDetector.topNames();
    }

    @GET
    @Path("names.txt")
    @Produces(MediaType.TEXT_PLAIN)
    public String frequenciesText() {
        StringBuilder names = new StringBuilder();
        twitterAccountNameFrequencyDetector.topNames().stream()
                .map(NameFrequency::getName)
                .forEach(n -> names.append(n).append("\n"));
        return names.toString();
    }

    @GET
    @Path("tweet-count.txt")
    @Produces(MediaType.TEXT_PLAIN)
    public long tweetCount() {
        return twitterAccountNameFrequencyDetector.tweetCount();
    }

    @GET
    @Path("user-count.txt")
    @Produces(MediaType.TEXT_PLAIN)
    public long userCount() {
        return twitterAccountNameFrequencyDetector.uniqueAccounts();
    }
}
