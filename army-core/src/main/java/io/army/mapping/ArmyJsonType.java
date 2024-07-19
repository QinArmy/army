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
import io.army.sqltype.DataType;

/**
 * <p>Package class
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link JsonType}</li>
 *     <li>{@link JsonbType}</li>
 * </ul>
 *
 * @since 0.6.0
 */
abstract class ArmyJsonType extends _ArmyBuildInType {

    final Class<?> javaType;

    /**
     * Package constructor
     */
    ArmyJsonType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }


    @Override
    public final Object convert(MappingEnv env, final Object source) throws CriteriaException {
        if (!(source instanceof String) && this.javaType.isInstance(source)) {
            return source;
        }
        if (!(source instanceof String)) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        Object documentValue;
        try {
            documentValue = env.jsonCodec().decode((String) source, this.javaType);
        } catch (Exception e) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, e);
        }
        if (documentValue == null) {
            documentValue = DOCUMENT_NULL_VALUE;
        }
        return documentValue;
    }

    @Override
    public final String beforeBind(DataType dataType, MappingEnv env, final Object source) {
        final String value;
        if (source instanceof String) {
            value = (String) source;
        } else if (this.javaType.isInstance(source)) {
            try {
                value = env.jsonCodec().encode(source);
            } catch (Exception e) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        return value;
    }

    @Override
    public final Object afterGet(DataType dataType, MappingEnv env, final Object source) {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final Class<?> javaType = this.javaType;
        if (javaType == String.class) {
            return source;
        }
        Object documentValue;

        try {
            documentValue = env.jsonCodec().decode((String) source, javaType);
        } catch (Exception e) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
        }

        if (documentValue == null) {
            documentValue = DOCUMENT_NULL_VALUE;
        }
        return documentValue;
    }


}
