package io.army.session;

public interface StmtOption extends StmtOptionSpec {


    boolean isPreferServerPrepare();

    /**
     * <p>{@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement or not .
     * <p>Default : false
     *
     * @return true : {@link io.army.dialect.DialectParser} Whether parse batch statement as multi-statement.
     */
    boolean isParseBatchAsMultiStmt();


    boolean isSupportTimeout();


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

    /**
     * <p>Get frequency to help driver caching server-prepared statement.
     * <p>Default : -1  in the implementation of jdbd-spi,so if you don't invoke this method,driver will ignore this option.
     * <p><Strong>NOTE</Strong>: JDBC don't support this option
     *
     * @return <ul>
     * <li>negative : no action</li>
     * <li>0 : never cache server-prepared statement,if have cached ,close server-prepared statement and delete cache</li>
     * <li>positive : representing frequency</li>
     * <li>{@link Integer#MAX_VALUE} : always cache server-prepared statement</li>
     * </ul>
     */
    int frequency();

    MultiStmtMode multiStmtMode();


    interface BuilderSpec<B> extends OptionBuilderSpec<B> {

        B frequency(int value);

        B timeoutMillis(int millis);

        B multiStmtMode(MultiStmtMode mode);

        B parseBatchAsMultiStmt(boolean yes);

        B preferServerPrepare(boolean yes);


    }


}
