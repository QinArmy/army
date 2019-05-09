package com.example.simple;

/**
 * created  on 2018/11/25.
 */
public class Herb {

    public enum Type {ANNUAL, PERENNIAL, BINNIAL}

    private final String name;

    private final Type type;

    public Herb(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
