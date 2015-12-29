package com.minorityhobbies.tnf.services;

import com.minorityhobbies.tnf.api.NameFrequency;
import com.minorityhobbies.tnf.core.DelimitedStreamTweetClient;
import com.minorityhobbies.tnf.core.TweetUserFirstNameParser;
import com.minorityhobbies.tnf.domain.TwitterAccountName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Startup
public class TwitterAccountNameFrequencyDetector {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Set<TwitterAccountName> accountsAlreadyProcessed = new HashSet<>();
    private final Map<String, Integer> frequency = new ConcurrentHashMap<>();
    private final AtomicLong tweetCount = new AtomicLong();
    private DelimitedStreamTweetClient delimitedStreamTweetClient;
    private TweetUserFirstNameParser tweetUserFirstNameParser;

    public TwitterAccountNameFrequencyDetector() {
        this.tweetUserFirstNameParser = new TweetUserFirstNameParser();
    }

    @Resource(mappedName = "config/accessToken")
    String accessToken;

    @PostConstruct
    public void init() {
        delimitedStreamTweetClient = new DelimitedStreamTweetClient(
                "http://localhost:8080/jacksonps4/api/twitter/s/statuses/sample.json?delimited=length",
                this::findAndStoreName, accessToken);
    }

    @PreDestroy
    public void stop() {
        if (delimitedStreamTweetClient != null) {
            delimitedStreamTweetClient.close();
        }
    }

    private void findAndStoreName(String tweetData) {
        try {
            TwitterAccountName twitterAccount = tweetUserFirstNameParser.apply(tweetData);
            if (twitterAccount != null && accountsAlreadyProcessed.add(twitterAccount)) {
                frequency.compute(twitterAccount.getName(),
                        (name, currentCount) -> currentCount == null ? 1 : currentCount + 1);
                tweetCount.incrementAndGet();
            }
        } catch (RuntimeException e) {
            logger.error("Failed to process tweet: " + tweetData, e);
        }
    }

    public List<NameFrequency> topNames() {
        return frequency.entrySet().stream()
                .map(e -> new NameFrequency(e.getKey(), e.getValue()))
                .sorted()
                .sorted(Comparator.reverseOrder())
                .limit(500)
                .collect(Collectors.toList());
    }

    public long tweetCount() {
        return tweetCount.get();
    }

    public long uniqueAccounts() {
        return accountsAlreadyProcessed.size();
    }

    @Schedules(@Schedule(hour = "*", minute = "*", persistent = false))
    public void logResults() {
        logger.info(String.format("Top names: %s", topNames()));
    }
}
