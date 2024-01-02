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

package io.army.sync;

import io.army.session.FactoryBuilderSpec;
import io.army.session.SessionFactoryException;

/**
 * <p>This interface representing the builder of {@link SyncSessionFactory}.
 * <p>The instance of This interface is created by {@link #builder()}.
 *
 * @since 0.6.0
 */
public interface SyncFactoryBuilder extends FactoryBuilderSpec<SyncFactoryBuilder, SyncSessionFactory> {


    @Override
    SyncSessionFactory build() throws SessionFactoryException;


    static SyncFactoryBuilder builder() {
        return ArmySyncFactoryBuilder.create();
    }


}
