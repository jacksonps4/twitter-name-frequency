package com.minorityhobbies.tnf.services;

import com.minorityhobbies.tnf.api.NameFrequency;
import com.minorityhobbies.tnf.core.DelimitedStreamTweetClient;
import com.minorityhobbies.tnf.core.TweetUserFirstNameParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Startup
public class TwitterAccountNameFrequencyDetector {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Integer> frequency = new ConcurrentHashMap<>();
    private final AtomicLong tweetCount = new AtomicLong();
    private DelimitedStreamTweetClient delimitedStreamTweetClient;
    private TweetUserFirstNameParser tweetUserFirstNameParser;

    public TwitterAccountNameFrequencyDetector() {
        this.tweetUserFirstNameParser = new TweetUserFirstNameParser();
    }

    @PostConstruct
    public void init() {
        delimitedStreamTweetClient = new DelimitedStreamTweetClient(
                "http://localhost:8080/jacksonps4/api/twitter/s/statuses/sample.json?delimited=length",
                this::findAndStoreName);
    }

    @PreDestroy
    public void stop() {
        if (delimitedStreamTweetClient != null) {
            delimitedStreamTweetClient.close();
        }
    }

    private void findAndStoreName(String tweetData) {
        try {
            String firstName = tweetUserFirstNameParser.apply(tweetData);
            if (firstName != null) {
                frequency.compute(firstName, (name, currentCount) -> currentCount == null ? 1 : currentCount + 1);
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

    @Schedules(@Schedule(hour = "*", minute = "*", persistent = false))
    public void logResults() {
        logger.info(String.format("Top names: %s", topNames()));
    }
}
