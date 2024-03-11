package io.army.util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StreamUtils {

    private StreamUtils() {
        throw new UnsupportedOperationException();
    }


    public static void ignore(Object o) {
        // no-op
    }


    @Nullable
    public static <R> R collectAtMostOneRow(Stream<R> stream) {
        try (Stream<R> s = stream) {
            final List<R> list;
            list = s.collect(Collectors.toCollection(_Collections::arrayListWithCapacity1));

            final R row;
            switch (list.size()) {
                case 0:
                    row = null;
                    break;
                case 1:
                    row = list.get(0);
                    break;
                default:
                    throw _Exceptions.nonSingleRow(list);
            }
            return row;
        }

    }


}
