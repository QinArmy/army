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

package io.army.criteria.postgre.mapping;

import io.army.criteria.CriteriaException;
import io.army.criteria.postgre.type.MySubRow;
import io.army.dialect.Database;
import io.army.dialect.UnsupportedDialectException;
import io.army.dialect._Constant;
import io.army.mapping.*;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.util.ArrayUtils;

import java.util.List;

public final class MySubRowType extends UserMappingType
        implements MappingType.SqlUserDefinedType, MappingType.SqlCompositeType {


    public static MySubRowType from(final Class<?> javaType) {
        if (javaType != MySubRow.class) {
            throw errorJavaType(MySubRowType.class, javaType);
        }
        return INSTANCE;
    }

    public static final MySubRowType INSTANCE = new MySubRowType();

    private static final DataType DATA_TYPE = DataType.from("SUBROWTYPE");

    private static final List<CompositeTypeField> FIELD_LIST = ArrayUtils.of(
            CompositeTypeField.from("d", IntegerType.INSTANCE),
            CompositeTypeField.from("e", TextType.INSTANCE)
    );

    private MySubRowType() {
    }

    @Override
    public Class<?> javaType() {
        return MySubRow.class;
    }

    @Override
    public DataType map(final ServerMeta meta) throws UnsupportedDialectException {
        if (meta.serverDatabase() != Database.PostgreSQL) {
            throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return DATA_TYPE;
    }

    @Override
    public <Z> MappingType compatibleFor(final DataType dataType, final Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public MySubRow convert(MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof MySubRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, this.map(env.serverMeta()), source, null);
        }
        return (MySubRow) source;
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/rowtypes.html#id-1.5.7.24.6">Constructing Composite Values</a>
     */
    @Override
    public String beforeBind(DataType type, MappingEnv env, Object source) throws CriteriaException {
        if (!(source instanceof MySubRow)) {
            throw PARAM_ERROR_HANDLER.apply(this, type, source, null);
        }
        final MySubRow row = (MySubRow) source;
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(_Constant.LEFT_PAREN);

        Object value;
        value = row.getNumber();
        if (value != null) {
            builder.append(value);
        }
        builder.append(_Constant.COMMA);

        value = row.getText();
        if (value != null) {
            builder.append(_Constant.DOUBLE_QUOTE)
                    .append(value)
                    .append(_Constant.DOUBLE_QUOTE);
        }
        return builder.append(_Constant.RIGHT_PAREN)
                .toString();
    }

    @Override
    public Object afterGet(DataType type, MappingEnv env, Object source) throws DataAccessException {
        //TODO
        throw new UnsupportedOperationException();
    }



    @Override
    public List<CompositeTypeField> fieldList() {
        return FIELD_LIST;
    }


}
