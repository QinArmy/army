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

import io.army.dialect.Database;

/**
 * Package interface
 */
public interface Operator {

    String name();

    String spaceRender();

    String spaceRender(Database database);

    @Override
    String toString();

    interface SqlUnaryOperator extends Operator {

    }

    interface SqlUnaryExpOperator extends SqlUnaryOperator {

    }

    interface SqlUnaryBooleanOperator extends SqlUnaryOperator {

    }


    interface SqlDualOperator extends Operator {


    }


    interface SqlDualBooleanOperator extends SqlDualOperator {


    }

    interface SqlDualExpressionOperator extends SqlDualOperator {


        int precedence();

    }


}
