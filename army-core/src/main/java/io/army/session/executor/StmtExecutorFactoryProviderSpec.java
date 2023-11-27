package io.army.session.executor;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing provider of {@link StmtExecutorFactory} spec.
 * This implementation of this interface must declared :
 * <pre>
 *      <code>
 *          public static {implementation class of StmtExecutorFactoryProviderSpec} create(Object datasource,String factoryName,ArmyEnvironment env){
 *
 *          }
 *      </code>
 *  </pre>
 * <p>This interface is base interface of following
 * <ul>
 *     <li>{@code io.army.sync.executor.SyncStmtExecutorFactoryProvider}</li>
 *     <li>{@code io.army.reactive.executor.ReactiveStmtExecutorFactoryProvider}</li>
 * </ul>
 * The sub interface must override following methods :
 * <ul>
 *     <li>{@link #createServerMeta(Dialect, Function)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface StmtExecutorFactoryProviderSpec {

    /**
     * Sub interface must override this method return value type.
     * <p>This method always is invoked before {@link #createFactory(ExecutorEnv)}
     */
    Object createServerMeta(Dialect useDialect, @Nullable Function<String, Database> func);

    /**
     * Sub interface must override this method return value type.
     * <p>This method always is invoked after {@link #createServerMeta(Dialect, Function)}
     */
    Object createFactory(ExecutorEnv env);


}
