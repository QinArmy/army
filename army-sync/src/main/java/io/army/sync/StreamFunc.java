package io.army.sync;

import io.army.util._Exceptions;

import javax.annotation.Nullable;

public abstract class StreamFunc {

    private StreamFunc() {
        throw new UnsupportedOperationException();
    }


    public static void ignore(Object o) {
        // no-op
    }


    public static <T> T atMostOne(@Nullable T prev, @Nullable T next) {
        throw _Exceptions.nonMono();
    }

    @Nullable
    public static <T> T first(@Nullable T prev, @Nullable T next) {
        return prev;
    }


    @Nullable
    public static <T> T last(@Nullable T prev, @Nullable T next) {
        return next;
    }

}
