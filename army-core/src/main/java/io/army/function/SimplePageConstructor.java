package io.army.function;

import java.util.List;

@FunctionalInterface
public interface SimplePageConstructor<T, R> {

    /**
     * <p>Create immutable page object
     *
     * @param pageData   a unmodified list   current page data.
     * @param pageSize   page size of each page
     * @param pageNumber current page number.
     * @return immutable page object
     */
    R apply(List<T> pageData, int pageSize, long pageNumber);


}
