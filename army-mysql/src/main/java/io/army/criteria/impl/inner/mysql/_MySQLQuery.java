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

package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.criteria.impl.inner._Statement;

import javax.annotation.Nullable;
import java.util.List;

public interface _MySQLQuery extends _Query,
        _DialectStatement,
        _Statement._WithClauseSpec,
        _Query._WindowClauseSpec,
        _Statement._LimitClauseSpec {

    boolean groupByWithRollUp();

    boolean orderByWithRollup();

    @Nullable
    _LockBlock lockBlock();

    List<String> intoVarList();




}
