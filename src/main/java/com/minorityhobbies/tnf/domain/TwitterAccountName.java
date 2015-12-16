package com.minorityhobbies.tnf.domain;


public class TwitterAccountName {
    private final String id;
    private final String name;

    public TwitterAccountName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitterAccountName that = (TwitterAccountName) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {

        return id;
    }

    public String getName() {
        return name;
    }
}
