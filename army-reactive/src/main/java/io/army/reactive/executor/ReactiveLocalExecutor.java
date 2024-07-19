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

package io.army.reactive.executor;


import io.army.option.CloseableSpec;

/**
 * <p>This interface representing local {@link ReactiveExecutor} that support local transaction.
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @since 0.6.0
 */
public interface ReactiveLocalExecutor extends ReactiveExecutor, ReactiveExecutor.LocalTransactionSpec {


}
