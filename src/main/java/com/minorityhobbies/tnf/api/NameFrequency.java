package com.minorityhobbies.tnf.api;

public class NameFrequency implements Comparable<NameFrequency> {
    private String name;
    private int frequency;

    public NameFrequency() {
    }

    public NameFrequency(String name, int frequency) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameFrequency that = (NameFrequency) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(NameFrequency o) {
        return new Integer(frequency).compareTo(o.frequency);
    }

    @Override
    public String toString() {
        return String.format("%s : %d", name, frequency);
    }
}
