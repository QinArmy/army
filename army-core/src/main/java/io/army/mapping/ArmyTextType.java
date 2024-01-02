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

package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;


/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link TinyTextType}</li>
 *     <li>{@link TextType}</li>
 *     <li>{@link MediumTextType}</li>
 * </ul>
 *
 * @since 0.6.0
 */
abstract class ArmyTextType extends _ArmyBuildInMapping implements MappingType.SqlTextType {


    ArmyTextType() {
    }

    @Override
    public final Class<?> javaType() {
        return String.class;
    }


    @Override
    public final String convert(MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        return StringType.toString(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public final String afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        return StringType.toString(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
