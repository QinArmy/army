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

package io.army.session;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Function;


/**
 * <p>This class representing server error message.
 * <p>Throw(or emit) when database server response error message.
 *
 * @since 0.6.0
 */
public final class ServerException extends DriverException implements OptionSpec {

    private final Function<Option<?>, ?> optionFunc;

    private final Set<Option<?>> optionSet;


    public ServerException(Throwable cause, @Nullable String sqlState, int vendorCode,
                           Function<Option<?>, ?> optionFunc, Set<Option<?>> optionSet) {
        super(cause, sqlState, vendorCode);
        this.optionFunc = optionFunc;
        this.optionSet = optionSet;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }

}
