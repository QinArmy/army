package io.army.session.executor;

import io.army.session.CloseableSpec;
import io.army.session.Option;
import io.army.session.OptionSpec;

import java.util.function.Function;

/**
 * <p>This interface representing {@link StmtExecutor} factory spec .
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code io.army.sync.executor.SyncExecutorFactory}</li>
 *     <li>{@code io.army.reactive.executor.ReactiveStmtExecutorFactory}</li>
 * </ul>
 * The sub interface must override following methods :
 * <ul>
 *     <li>{@link #metaExecutor(Function)}</li>
 *     <li>{@link #localExecutor(String, boolean, Function)}</li>
 *     <li>{@link #rmExecutor(String, boolean, Function)}</li>
 * </ul>
 * @since 0.6.0
 */
public interface ExecutorFactory extends CloseableSpec, OptionSpec {


    /**
     * @return true : underlying database driver provider save point spi.
     */
    boolean supportSavePoints();

    /**
     * <p>For example:
     * <ul>
     *     <li>io.jdbd</li>
     *     <li>java.sql</li>
     * </ul>
     *
     * @return driver spi vendor,The value returned typically is the package name for this vendor.
     */
    String driverSpiVendor();


    /**
     * <p>For example: io.army
     *
     * @return executor vendor,The value returned typically is the package name for this vendor.
     */
    String executorVendor();


    /**
     * Sub interface must override this method return value type.
     */
    Object metaExecutor(Function<Option<?>, ?> optionFunc);


    /**
     * Sub interface must override this method return value type.
     *
     * @param sessionName {@link io.army.session.Session}'s name.
     */
    Object localExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc);


    /**
     * Sub interface must override this method return value type.
     *
     * @param sessionName {@link io.army.session.Session}'s name.
     */
    Object rmExecutor(String sessionName, boolean readOnly, Function<Option<?>, ?> optionFunc);

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>session factory name</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
