package io.army.session;

public interface StmtOption {

    boolean isPreferServerPrepare();


    boolean isSupportTimeout();


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


}
