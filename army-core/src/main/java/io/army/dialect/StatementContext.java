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

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.session.Option;
import io.army.session.Session;
import io.army.session.SessionSpec;
import io.army.stmt.MultiParam;
import io.army.stmt.SingleParam;
import io.army.stmt.StmtParams;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * <p>
 * This class is base class of all the implementation of {@link  _SqlContext}.
 *
 * @since 0.6.0
 */
abstract class StatementContext implements _StmtContext, StmtParams {

    static final String SPACE_PLACEHOLDER = " ?";


    protected final ArmyParser parser;

    protected final SessionSpec sessionSpec;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    private final ParamAccepter paramAccepter;


    private SessionSpec sessionWrapper;

    protected StatementContext(ArmyParser parser, SessionSpec sessionSpec) {
        this(null, parser, sessionSpec);
    }


    protected StatementContext(StatementContext outerContext) {
        this(outerContext, outerContext.parser, outerContext.sessionSpec);
    }

    protected StatementContext(@Nullable StatementContext parentOrOuterContext, ArmyParser parser, SessionSpec sessionSpec) {
        if (parentOrOuterContext == null) {
            this.parser = parser;
            this.sessionSpec = sessionSpec;
            this.visible = sessionSpec.visible();
            this.sqlBuilder = new StringBuilder(128);
        } else {
            this.parser = parentOrOuterContext.parser;
            this.visible = parentOrOuterContext.visible;
            this.sessionSpec = parentOrOuterContext.sessionSpec;
            this.sqlBuilder = parentOrOuterContext.sqlBuilder;
        }

        if (this instanceof _InsertContext && !(this instanceof _QueryInsertContext)) {
            if (parentOrOuterContext == null) {
                this.paramAccepter = new ParamAccepter(this::readCurrentRowNamedValue, this::readCurrentBatchIndex);
            } else {
                this.paramAccepter = new ParamAccepterWithOuter(parentOrOuterContext.paramAccepter,
                        this::readCurrentRowNamedValue, this::readCurrentBatchIndex);
            }
        } else if (parentOrOuterContext instanceof MultiStmtContext && this instanceof NarrowDmlContext) {
            this.paramAccepter = new ParamAccepterWithOuter(parentOrOuterContext.paramAccepter,
                    this::readCurrentRowNamedValue, this::readCurrentBatchIndex);
        } else if (parentOrOuterContext == null) {
            this.paramAccepter = new ParamAccepter(null, null);
        } else {
            this.paramAccepter = parentOrOuterContext.paramAccepter;
        }

    }


    @Override
    public final Database database() {
        return this.parser.dialectDatabase;
    }

    @Override
    public final Dialect dialect() {
        return this.parser.dialect;
    }


    @Override
    public final void appendFuncName(final boolean buildIn, final String name) {
        final String lowerCaseName, upperCaseName;
        if (buildIn) {
            lowerCaseName = upperCaseName = null;
        } else if (this.parser.keyWordMap.containsKey(upperCaseName = name.toUpperCase(Locale.ROOT))) {
            String m = String.format("User defined function name[%s] is key word for %s", name, this.parser.dialect);
            throw new CriteriaException(m);
        } else if (!name.equals(lowerCaseName = name.toLowerCase(Locale.ROOT)) && !name.equals(upperCaseName)) {
            String m = String.format("User defined function name[%s] is CamelCase", name);
            throw new CriteriaException(m);
        } else if (!_DialectUtils.isSimpleIdentifier(name)) {
            String m = String.format("User defined function name[%s] isn't simple identifier", name);
            throw new CriteriaException(m);
        }

        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE);
        switch (this.parser.funcNameMode) {
            case DEFAULT:
                sqlBuilder.append(name);
                break;
            case LOWER_CASE: {
                if (lowerCaseName == null) {
                    sqlBuilder.append(name.toLowerCase(Locale.ROOT));
                } else {
                    sqlBuilder.append(lowerCaseName);
                }
            }
            break;
            case UPPER_CASE: {
                if (upperCaseName == null) {
                    sqlBuilder.append(name.toUpperCase(Locale.ROOT));
                } else {
                    sqlBuilder.append(upperCaseName);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(this.parser.funcNameMode);
        }


    }

    @Override
    public void appendFieldOnly(FieldMeta<?> field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void appendSubQuery(final SubQuery query) {
        this.parser.handleSubQuery(query, this);
    }

    @Override
    public final DialectParser parser() {
        return this.parser;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }


    @Override
    public final void appendParam(final SQLParam sqlParam) {
        final ArrayList<SQLParam> paramList = this.paramAccepter.paramList;
        if (sqlParam instanceof SingleParam) {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            paramList.add(sqlParam);
        } else if (sqlParam instanceof MultiParam) {
            appendMultiParamPlaceholder(this.sqlBuilder, (SqlValueParam.MultiValue) sqlParam);
            paramList.add(sqlParam);
        } else if (sqlParam instanceof NamedParam.NamedSingle) {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            if (this.paramAccepter.nameValueFunc == null) {
                paramList.add(sqlParam);
                if (!this.paramAccepter.hasNamedParam) {
                    this.paramAccepter.setHasNamedParam();
                }
            } else if (sqlParam == SQLs.BATCH_NO_PARAM) {
                final IntSupplier batchNoFunc = this.paramAccepter.batchIndexFunc;
                assert batchNoFunc != null;
                paramList.add(SingleParam.build(sqlParam.typeMeta(), batchNoFunc.getAsInt() + 1));
            } else {
                paramList.add(SingleParam.build(sqlParam.typeMeta(), readNamedValue((NamedParam) sqlParam)));
            }
        } else if (sqlParam instanceof NamedParam.NamedRow) {
            appendMultiParamPlaceholder(this.sqlBuilder, (SqlValueParam.MultiValue) sqlParam);
            if (this.paramAccepter.nameValueFunc == null) {
                paramList.add(sqlParam);
                if (!this.paramAccepter.hasNamedParam) {
                    this.paramAccepter.setHasNamedParam();
                }
            } else {
                final NamedParam.NamedRow namedMulti = (NamedParam.NamedRow) sqlParam;
                paramList.add(MultiParam.build(namedMulti, readNamedMulti(namedMulti)));
            }
        } else {
            //no bug,never here
            throw new IllegalArgumentException();
        }

    }

    @Override
    public final void appendLiteral(final TypeMeta typeMeta, final @Nullable Object value, boolean typeName) {
        if (!this.paramAccepter.hasLiteral) {
            this.paramAccepter.setHasLiteral();
        }
        this.parser.safeLiteral(typeMeta, value, typeName, this.sqlBuilder.append(_Constant.SPACE));

    }

    @Override
    public final void appendLiteral(final NamedLiteral namedLiteral, final boolean typeName) {

        final Function<String, Object> nameValueFunc;
        final Object value;
        if (namedLiteral == SQLs.BATCH_NO_LITERAL || namedLiteral == SQLs.BATCH_NO_CONST) {
            final IntSupplier batchNoFunc = this.paramAccepter.batchIndexFunc;
            if (batchNoFunc == null) {
                String m = String.format("%s don't support batch no literal/const", this.getClass().getName());
                throw new CriteriaException(m);
            }
            value = batchNoFunc.getAsInt() + 1;
        } else if ((nameValueFunc = this.paramAccepter.nameValueFunc) == null) {
            String m = String.format("%s don't support batch no literal/const", this.getClass().getName());
            throw new CriteriaException(m);
        } else {
            value = nameValueFunc.apply(namedLiteral.name());
        }


        if (!this.paramAccepter.hasNamedLiteral) {
            this.paramAccepter.setHasNamedLiteral();
        }

        final StringBuilder sqlBuilder = this.sqlBuilder;

        if (value == null) {
            if (namedLiteral instanceof SqlValueParam.NonNullValue) {
                String m = String.format("named literal(%s) must non-null.", namedLiteral.name());
                throw new CriteriaException(m);
            }

            if (this.parser.serverDatabase == Database.PostgreSQL) {
                sqlBuilder.append(_Constant.SPACE);
                this.parser.safeLiteral(namedLiteral.typeMeta(), null, typeName, sqlBuilder);
            } else {
                sqlBuilder.append(_Constant.SPACE_NULL);
            }
        } else if (namedLiteral instanceof SqlValueParam.SingleValue) {
            sqlBuilder.append(_Constant.SPACE);
            this.parser.safeLiteral(namedLiteral.typeMeta(), value, typeName, sqlBuilder);
        } else if (!(namedLiteral instanceof SqlValueParam.NamedMultiValue)) {
            //no bug,never here
            throw new IllegalArgumentException();
        } else if (!(value instanceof Collection)) {
            throw _Exceptions.namedParamNotMatch((SqlValueParam.NamedMultiValue) namedLiteral, value);
        } else if (((Collection<?>) value).size() == ((SqlValueParam.NamedMultiValue) namedLiteral).columnSize()) {

            final ArmyParser parser = this.parser;
            final TypeMeta typeMeta = namedLiteral.typeMeta();
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            int i = 0;
            for (Object v : (Collection<?>) value) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    sqlBuilder.append(_Constant.SPACE);
                }
                parser.safeLiteral(typeMeta, v, typeName, sqlBuilder);
                i++;
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        } else {
            throw _Exceptions.namedMultiParamSizeError((SqlValueParam.NamedMultiValue) namedLiteral,
                    ((Collection<?>) value).size());
        }

    }

    @Override
    public final StringBuilder identifier(String identifier, StringBuilder builder) {
        return this.parser.identifier(identifier, builder);
    }

    @Override
    public final String identifier(String identifier) {
        return this.parser.identifier(identifier);
    }

    @Override
    public final boolean hasParam() {
        return this.paramAccepter.paramList.size() > 0;
    }


    @Override
    public final boolean hasNamedLiteral() {
        return this.paramAccepter.hasNamedLiteral;
    }

    @Override
    public final boolean hasLiteral() {
        return this.paramAccepter.hasLiteral;
    }

    @Override
    public final boolean isUpdateTimeOutputParam() {
        final ParamAccepter accepter = this.paramAccepter;
        return accepter.paramList.size() > 0 || !accepter.hasLiteral;
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public final SessionSpec sessionSpec() {
        SessionSpec sessionSpec = this.sessionSpec;
        if (sessionSpec instanceof Session) {
            sessionSpec = this.sessionWrapper;
            if (sessionSpec == null) {
                this.sessionWrapper = sessionSpec = new SessionWrapper(this.sessionSpec);
            }
        }
        return sessionSpec;
    }


    @Override
    public final String sql() {
        return this.sqlBuilder.toString();
    }

    @Override
    public final List<SQLParam> paramList() {
        return _Collections.unmodifiableList(this.paramAccepter.paramList);
    }

    @Override
    public List<? extends Selection> selectionList() {
        throw new UnsupportedOperationException();
    }

    final boolean hasNamedParam() {
        return this.paramAccepter.hasNamedParam;
    }

    /**
     * @see InsertContext#appendSetLeftItem(SqlField, Expression)
     * @see SingleUpdateContext#appendSetLeftItem(SqlField, Expression)
     * @see MultiUpdateContext#appendSetLeftItem(SqlField, Expression)
     */
    final void appendUpdateTimePlaceholder(final FieldMeta<?> updateTime, final Expression updateTimePlaceholder) {
        if (!_MetaBridge.UPDATE_TIME.equals(updateTime.fieldName())) {
            final String m = String.format("Expression %s present in error context", updateTimePlaceholder);
            throw new CriteriaException(m);
        } else if (updateTimePlaceholder != SQLs.UPDATE_TIME_PLACEHOLDER) {
            // no bug,never here
            throw _Exceptions.illegalExpression(updateTimePlaceholder);
        }

        this.sqlBuilder.append(_Constant.SPACE_EQUAL_SPACE);

        final Temporal updateTimeValue;
        updateTimeValue = this.parser.createUpdateTimeValue(updateTime);

        if (isUpdateTimeOutputParam()) {
            appendParam(SingleParam.build(updateTime, updateTimeValue));
        } else {
            appendLiteral(updateTime, updateTimeValue, true);
        }
    }


    @Nullable
    Object readCurrentRowNamedValue(String name) {
        String m = String.format("context[%s] don't support named value.", this.getClass().getName());
        throw new CriteriaException(m);
    }

    /**
     * @return batch index(based zero)
     */
    int readCurrentBatchIndex() {
        String m = String.format("context[%s] don't batchNo.", this.getClass().getName());
        throw new CriteriaException(m);
    }


    @Nullable
    private Object readNamedValue(final NamedParam namedParam) {
        final Function<String, Object> function = this.paramAccepter.nameValueFunc;
        assert function != null;
        final Object value;
        value = function.apply(namedParam.name());
        if (value == null && namedParam instanceof SqlValueParam.NonNullValue) {
            throw _Exceptions.nonNullNamedParam(namedParam);
        }
        return value;
    }

    private Collection<?> readNamedMulti(final NamedParam.NamedRow namedParam) {
        final Object value;
        value = readNamedValue(namedParam);
        if (!(value instanceof Collection)) {
            throw _Exceptions.namedParamNotMatch(namedParam, value);
        }
        return (Collection<?>) value;
    }


    static IllegalStateException nonTopContext() {
        return new IllegalStateException("Non-top context couldn't create stmt.");
    }


    private static void appendMultiParamPlaceholder(final StringBuilder sqlBuilder,
                                                    final SqlValueParam.MultiValue sqlParam) {
        final int paramSize;
        paramSize = sqlParam.columnSize();
        assert paramSize > 0;
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        for (int i = 0; i < paramSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(SPACE_PLACEHOLDER);
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    private static class ParamAccepter {

        private final ArrayList<SQLParam> paramList;

        private final Function<String, Object> nameValueFunc;

        private final IntSupplier batchIndexFunc;

        private boolean hasNamedParam;

        private boolean hasNamedLiteral;

        private boolean hasLiteral;

        private ParamAccepter(@Nullable Function<String, Object> nameValueFunc, @Nullable IntSupplier batchIndexFunc) {
            assert (nameValueFunc == null) == (batchIndexFunc == null);
            this.paramList = _Collections.arrayList();
            this.nameValueFunc = nameValueFunc;
            this.batchIndexFunc = batchIndexFunc;
        }

        private ParamAccepter(ParamAccepter outerConsumer, Function<String, Object> nameValueFunc,
                              IntSupplier batchIndexFunc) {

            this.paramList = outerConsumer.paramList;
            this.hasNamedParam = outerConsumer.hasNamedParam;
            this.hasNamedLiteral = outerConsumer.hasNamedLiteral;
            this.hasLiteral = outerConsumer.hasLiteral;

            this.nameValueFunc = nameValueFunc;
            this.batchIndexFunc = batchIndexFunc;
        }

        final void setHasNamedParam() {
            this.hasNamedParam = true;
            if (this instanceof ParamAccepterWithOuter) {
                ((ParamAccepterWithOuter) this).outerAccepter.setHasNamedParam();
            }
        }

        final void setHasNamedLiteral() {
            this.hasLiteral = this.hasNamedLiteral = true;
            if (this instanceof ParamAccepterWithOuter) {
                ((ParamAccepterWithOuter) this).outerAccepter.setHasNamedLiteral();
            }
        }

        final void setHasLiteral() {
            this.hasLiteral = true;
            if (this instanceof ParamAccepterWithOuter) {
                ((ParamAccepterWithOuter) this).outerAccepter.setHasLiteral();
            }
        }


    } // ParamConsumer


    private static final class ParamAccepterWithOuter extends ParamAccepter {

        private final ParamAccepter outerAccepter;

        private ParamAccepterWithOuter(ParamAccepter outerAccepter, Function<String, Object> function,
                                       IntSupplier supplier) {
            super(outerAccepter, function, supplier);
            this.outerAccepter = outerAccepter;
        }

    } // ParamConsumerWithOuter


    private static final class SessionWrapper implements SessionSpec {

        private final SessionSpec sessionSpec;

        private SessionWrapper(SessionSpec sessionSpec) {
            this.sessionSpec = sessionSpec;
        }

        @Nullable
        @Override
        public <T> T valueOf(Option<T> option) {
            return this.sessionSpec.valueOf(option);
        }

        @Override
        public String name() {
            return this.sessionSpec.name();
        }

        @Override
        public Visible visible() {
            return this.sessionSpec.visible();
        }

    } // SessionWrapper


}
