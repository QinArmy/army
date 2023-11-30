package io.army.datasource;


import io.army.session.Option;

import java.util.function.Function;


/**
 * <p>This interface representing routing DataSource for Read/Write Splitting.
 *
 * @param <R> the java type of DataSource
 * @since 1.0
 */
public interface ReadWriteSplittingDataSource<R> {

    /**
     * <p>Select one writable DataSource
     * <p>The implementation of this method perhaps support some of following :
     * <ul>
     *     <li>{@link Option#NAME} : database session name,if driver api support this option</li>
     *     <li>{@link Option#TIMEOUT_MILLIS} : transaction timeout</li>
     *     <li>{@link Option#LOCK_TIMEOUT_MILLIS : max lock time}</li>
     * </ul>
     * above options can help application developer to select optimal DataSource.
     * <p>Above options is passed by application developer with following methods :
     * <ul>
     *     <li>{@link io.army.session.SessionFactory.SessionBuilderSpec#dataSourceOption(Option, Object)} </li>
     *     <li>{@link io.army.session.FactoryBuilderSpec#dataSourceOption(Option, Object)}</li>
     * </ul>
     *
     * @param func option function, default {@link Option#EMPTY_FUNC}
     * @return a DataSource that can support read/write
     */
    R writableDataSource(final Function<Option<?>, ?> func);


    /**
     * <p>Select one readonly DataSource
     * <p>The implementation of this method perhaps support some of following :
     * <ul>
     *     <li>{@link Option#NAME} : database session name,if driver api support this option</li>
     *     <li>{@link Option#TIMEOUT_MILLIS} : transaction timeout</li>
     *     <li>{@link Option#LOCK_TIMEOUT_MILLIS : max lock time}</li>
     * </ul>
     * above options can help application to select optimal DataSource.
     * <p>Above options is passed by application developer with following methods :
     * <ul>
     *     <li>{@link io.army.session.SessionFactory.SessionBuilderSpec#dataSourceOption(Option, Object)} </li>
     *     <li>{@link io.army.session.FactoryBuilderSpec#dataSourceOption(Option, Object)}</li>
     * </ul>l
     *
     * @param func option function, default {@link Option#EMPTY_FUNC}
     * @return a readonly DataSource
     */
    R readOnlyDataSource(final Function<Option<?>, ?> func);


}
