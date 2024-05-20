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

package io.army.criteria.mysql.inner;


import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.mysql.MySQLs;

import java.util.List;

public interface _MySQLUpdate extends _Update, _DialectStatement, _Statement._WithClauseSpec {


    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();


}
