/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.executor;

import io.army.dialect.Database;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This interface representing provider of {@link ExecutorFactory} spec.
 * This implementation of this interface must declared :
 * <pre>
 *      <code>
 *          public static {implementation class of StmtExecutorFactoryProviderSpec} create(Object datasource,String factoryName,ArmyEnvironment env){
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
 *     <li>{@link #createServerMeta(Function)}</li>
 *     <li>{@link #createFactory(ExecutorEnv)}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface ExecutorFactoryProvider {

    /**
     * Sub interface must override this method return value type.
     * <p>This method always is invoked before {@link #createFactory(ExecutorEnv)}
     */
    Object createServerMeta(@Nullable Function<String, Database> func);

    /**
     * Sub interface must override this method return value type.
     * <p>This method always is invoked after {@link #createServerMeta(Function)}
     */
    Object createFactory(ExecutorEnv env);


}
