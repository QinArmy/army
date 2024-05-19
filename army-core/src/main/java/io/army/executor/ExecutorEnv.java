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

import io.army.codec.FieldCodec;
import io.army.env.ArmyEnvironment;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.record.ResultRecord;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public interface ExecutorEnv {

    String factoryName();

    ServerMeta serverMeta();

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap();


    ArmyEnvironment environment();

    SchemaMeta schemaMeta();

    /**
     * @return always same instance
     */
    MappingEnv mappingEnv();

    /**
     * @see io.army.session.FactoryBuilderSpec#columnConverterFunc(Function)
     * @see ResultRecord#get(int, Class)
     */
    @Nullable
    Function<Class<?>, Function<Object, ?>> converterFunc();

}
