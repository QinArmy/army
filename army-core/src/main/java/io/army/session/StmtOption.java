package io.army.session;

import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public interface StmtOption {

    boolean isPreferServerPrepare();


    boolean isSupportTimeout();

    /**
     * <p>{@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement or not .
     * <p>Default : false
     *
     * @return true : {@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement.
     */
    boolean isParseBatchAsMultiStmt();


    int timeoutMillSeconds();

    /**
     * <p>Get reset timeout seconds.
     *
     * @throws IllegalStateException throw when {@link #isSupportTimeout()} return false
     * @throws TimeoutException      throw  {@link #isSupportTimeout()} return true and reset time is zero.
     * @see #isSupportTimeout()
     */
    int restSeconds() throws TimeoutException;

    /**
     * <p>Get reset timeout mill seconds.
     *
     * @throws IllegalStateException throw when {@link #isSupportTimeout()} return false
     * @throws TimeoutException      throw  {@link #isSupportTimeout()} return true and reset time is zero.
     * @see #isSupportTimeout()
     */
    int restMillSeconds() throws TimeoutException;

    int fetchSize();

    MultiStmtMode multiStmtMode();


    Consumer<ResultStates> stateConsumer();


}
