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

package io.army.criteria.postgre.inner;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._JoinableDelete;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.standard.SQLs;

import javax.annotation.Nullable;

public interface _PostgreDelete extends _SingleDelete,
        _JoinableDelete,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Statement._ReturningListSpec,
        _Statement._WithDmlSpec {

    @Nullable
    SQLs.WordOnly modifier();

    @Nullable
    SQLs.SymbolAsterisk symbolAsterisk();


}
