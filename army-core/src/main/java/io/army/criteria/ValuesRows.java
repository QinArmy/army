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

package io.army.criteria;

import io.army.criteria.standard.SQLs;

import java.util.function.Consumer;

/**
 * <p>This interface representing dynamic row clause in  VALUES statement.
 * <p>This interface is similar to {@link ValuesParens}, except method name
 *
 * @see ValuesParens
 */
public interface ValuesRows {


    ValuesRows row(Consumer<Values._ValueStaticColumnSpaceClause> consumer);

    /**
     * <p>Create new row
     */
    ValuesRows row(SQLs.SymbolSpace space, Consumer<Values._ValuesDynamicColumnClause> consumer);

}
