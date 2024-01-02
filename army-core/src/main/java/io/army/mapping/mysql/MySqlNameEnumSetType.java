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

package io.army.mapping.mysql;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class MySqlNameEnumSetType extends _ArmyNoInjectionMapping implements MultiGenericsMappingType {

    private static final ConcurrentMap<Class<?>, MySqlNameEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySqlNameEnumSetType forElements(Class<?> fieldType, Class<?>[] elementTypes) {
        if (fieldType != Set.class) {
            throw errorJavaType(MySqlNameEnumSetType.class, fieldType);
        } else if (elementTypes.length != 1 || !elementTypes[0].isEnum()) {
            throw errorJavaType(MySqlNameEnumSetType.class, elementTypes[0]);
        }
        return INSTANCE_MAP.computeIfAbsent(elementTypes[0], MySqlNameEnumSetType::new);
    }


    private final List<Class<?>> elementTypes;

    private MySqlNameEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
    }

    @Override
    public Class<?> javaType() {
        return Set.class;
    }

    @Override
    public List<Class<?>> genericsTypeList() {
        return this.elementTypes;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        if (meta.serverDatabase() != Database.MySQL) {
            throw noMappingError(meta);
        }
        return MySQLType.SET;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        return null;
    }

    @Override
    public String beforeBind(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof Set)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final StringBuilder builder = new StringBuilder();
        final Class<?> elementJavaType = this.elementTypes.get(0);
        int index = 0;
        for (Object e : (Set<?>) source) {
            if (!elementJavaType.isInstance(e)) {
                throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            if (index > 0) {
                builder.append(_Constant.COMMA);
            }
            builder.append(((Enum<?>) e).name());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        try {
            return parseToSet(this.elementTypes.get(0), (String) source);
        } catch (IllegalArgumentException e) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
        }
    }


    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> Set<E> parseToSet(Class<?> javaType, String values) {
        final String[] array = values.split(",");
        final Set<E> set = new HashSet<>((int) (array.length / 0.75F));
        for (String e : array) {
            set.add(Enum.valueOf((Class<E>) javaType, e));
        }
        return set;
    }


}
