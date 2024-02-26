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

package io.army.dialect;

import io.army.criteria.NamedLiteral;
import io.army.criteria.SQLParam;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.meta.TypeMeta;
import io.army.session.SessionSpec;

import javax.annotation.Nullable;

/**
 * <p>
 * This interface representing sql context,that is used by {@link  DialectParser} and the implementation of criteria api,
 * for example {@link  io.army.criteria.impl.inner._Expression}.
 * * @since 0.6.0
 */
public interface _SqlContext extends SqlContextSpec {


    Database database();

    Dialect dialect();


    void appendFuncName(boolean buildIn, String name);



    void appendSubQuery(SubQuery query);

    DialectParser parser();

    StringBuilder sqlBuilder();

    /**
     * <p>
     * This method is designed for parameter expression.
     *     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append '?' to {@link #sqlBuilder()}</li>
     *         <li>append sqlParam to param list</li>
     *     </ol>
     *     */
    void appendParam(SQLParam sqlParam);

    void appendLiteral(TypeMeta typeMeta, @Nullable Object value, boolean typeName);

    void appendLiteral(NamedLiteral namedLiteral, boolean typeName);

    /**
     * @see DialectParser#identifier(String, StringBuilder)
     */
    StringBuilder identifier(String identifier, StringBuilder builder);

    /**
     * @see DialectParser#identifier(String)
     */
    String identifier(String identifier);

    Visible visible();

    SessionSpec sessionSpec();


}
