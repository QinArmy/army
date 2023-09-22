package io.army.session;

import io.army.lang.Nullable;

import java.util.function.Consumer;

public interface ResultStates extends ResultItem, OptionSpec {

    Consumer<ResultStates> IGNORE_STATES = states -> {
    };


    boolean inTransaction();

    long affectedRows();

    /**
     * @return empty or  success info(maybe contain warning info)
     */
    String message();

    boolean hasMoreResult();


    /**
     * @return true representing exists server cursor and the last row don't send.
     */
    boolean hasMoreFetch();

    /**
     * @return <ul>
     * <li>true : this instance representing the terminator of query result (eg: SELECT command)</li>
     * <li>false : this instance representing the update result (eg: INSERT/UPDATE/DELETE command)</li>
     * </ul>
     */
    boolean hasColumn();

    /**
     * @return the row count.<ul>
     * <li>If use fetch (eg: {@code  Statement#setFetchSize(int)} , {@code  RefCursor}) , then the row count representing only the row count of current fetch result.</li>
     * <li>Else then the row count representing the total row count of query result.</li>
     * </ul>
     */
    long rowCount();


    @Nullable
    Warning warning();

}
