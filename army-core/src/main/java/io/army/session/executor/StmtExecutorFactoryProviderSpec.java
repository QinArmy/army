package io.army.session.executor;

import io.army.dialect.Dialect;
import io.army.executor.ExecutorEnv;

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
 *     <li>{@link #createServerMeta(Dialect)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 *
 * @since 1.0
 */
public interface StmtExecutorFactoryProviderSpec {

    /**
     * Sub interface must override this method return value type.
     */
    Object createServerMeta(Dialect useDialect);

    /**
     * Sub interface must override this method return value type.
     */
    Object createFactory(ExecutorEnv env);


}
