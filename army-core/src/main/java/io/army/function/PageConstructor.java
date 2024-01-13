package io.army.function;

import java.util.List;

@FunctionalInterface
public interface PageConstructor<T, R> {

    /**
     * <p>Create immutable page object
     *
     * @param pageData      a unmodified list   current page data.
     * @param totalRowCount total row count
     * @param pageSize      page size of each page
     * @param pageNumber    current page number.
     * @param pageCount     page count.
     * @return immutable page object
     */
    R apply(List<T> pageData, long totalRowCount, int pageSize, long pageNumber, long pageCount);


}
