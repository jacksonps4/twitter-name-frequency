package com.minorityhobbies.tnf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DelimitedStreamTweetClient implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final URI uri;
    private final Consumer<String> consumer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DelimitedStreamTweetClient(String uri, Consumer<String> tweetDataConsumer) {
        this.uri = URI.create(uri);
        this.consumer = tweetDataConsumer;
        executor.submit(this::start);
    }

    private void start() {
        InputStream in = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestProperty("Cookie", "jacksonps4-signon=19374881-A2wL5ImOmnN4Li4W9eKfDx9AOXeQnWxo7jxi50tHo");
            in = conn.getInputStream();
        } catch (IOException e) {
            logger.error("Failed to establish connection to read tweets", e);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while (!Thread.currentThread().isInterrupted()) {
                String line = reader.readLine();
                if (line == null) {
                    logger.info("End of stream reached");
                    return;
                }

                if (line.trim().length() > 0) {
                    Integer tweetLength = Integer.parseInt(line);
                    byte[] b = new byte[tweetLength];
                    for (int i = 0; i < b.length; i++) {
                        b[i] = (byte) reader.read();
                    }
                    String tweetData = new String(b);
                    consumer.accept(tweetData);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read tweets", e);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Interrupted waiting for thread to stop");
        }
    }
}
