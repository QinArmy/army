package io.army.sync;

import io.army.util._Exceptions;

public abstract class StreamFunc {

    private StreamFunc() {
        throw new UnsupportedOperationException();
    }


    public static void ignore(Object o) {
        // no-op
    }


    public static <T> T atMostOne(T prev, T next) {
        throw _Exceptions.nonMono();
    }


    public static <T> T first(T prev, T next) {
        return prev;
    }


    public static <T> T last(T prev, T next) {
        return next;
    }


}
