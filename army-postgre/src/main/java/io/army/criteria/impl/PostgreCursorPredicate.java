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

package io.army.criteria.impl;

import io.army.dialect._SqlContext;
import io.army.util._StringUtils;

final class PostgreCursorPredicate extends OperationPredicate.OperationCompoundPredicate {


    private final String cursorName;

    PostgreCursorPredicate(String cursorName) {
        this.cursorName = cursorName;
    }

    @Override
    public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

        sqlBuilder.append(" CURRENT OF ");
        context.parser().identifier(this.cursorName, sqlBuilder);
    }

    @Override
    public int hashCode() {
        return this.cursorName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof PostgreCursorPredicate) {
            match = ((PostgreCursorPredicate) obj).cursorName.equals(this.cursorName);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(" CURRENT OF ")
                .append(this.cursorName)
                .toString();
    }


}
