package com.minorityhobbies.tnf.web;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class TwitterNameFrequencyApp extends Application {
    private final Set<Class<?>> classes = new HashSet<>();

    public TwitterNameFrequencyApp() {
        classes.add(TwitterNameFrequencyResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
