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
import io.army.struct.TextEnum;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MySQLTextEnumSetType extends _ArmyBuildInType implements MultiGenericsMappingType {

    private static final ConcurrentMap<Class<?>, MySQLTextEnumSetType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static MySQLTextEnumSetType fromSet(final Class<?> fieldType, final Class<?> elementTypes) {
        throw new UnsupportedOperationException();
    }

    private final List<Class<?>> elementTypes;

    private final Map<String, ? extends TextEnum> textEnumMap;

    private MySQLTextEnumSetType(Class<?> elementJavaType) {
        this.elementTypes = Collections.singletonList(elementJavaType);
        this.textEnumMap = TextEnum.getTextToEnumMap(elementJavaType);
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
            builder.append(((TextEnum) e).text());
            index++;
        }
        return builder.toString();
    }

    @Override
    public Set<?> afterGet(DataType dataType, MappingEnv env, Object source) {
        if (!(source instanceof String)) {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
        }
        final String[] array = ((String) source).split(",");
        final Set<TextEnum> set = new HashSet<>((int) (array.length / 0.75F));
        TextEnum textEnum;
        final Map<String, ? extends TextEnum> textEnumMap = this.textEnumMap;
        for (String text : array) {
            textEnum = textEnumMap.get(text);
            if (textEnum == null) {
                String m = String.format("%s unknown text[%s] instance.", elementTypes.get(0).getName(), text);
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, new IllegalArgumentException(m));
            }
            set.add(textEnum);
        }
        return set;
    }


}
