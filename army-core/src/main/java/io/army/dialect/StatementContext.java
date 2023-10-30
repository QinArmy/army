package io.army.dialect;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.MultiParam;
import io.army.stmt.SingleParam;
import io.army.stmt.StmtParams;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * <p>
 * This class is base class of all the implementation of {@link  _SqlContext}.
 * </p>
 *
 * @since 1.0
 */
abstract class StatementContext implements _PrimaryContext, StmtParams {

    static final String SPACE_PLACEHOLDER = " ?";

    protected final ArmyParser parser;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    private final ParamAccepter paramAccepter;

    protected StatementContext(ArmyParser parser, Visible visible) {
        this(null, parser, visible);
    }


    protected StatementContext(StatementContext outerContext) {
        this(outerContext, outerContext.parser, outerContext.visible);
    }

    protected StatementContext(@Nullable StatementContext parentOrOuterContext, ArmyParser parser, Visible visible) {
        if (parentOrOuterContext == null) {
            this.parser = parser;
            this.visible = visible;
            this.sqlBuilder = new StringBuilder(128);
        } else {
            this.parser = parentOrOuterContext.parser;
            this.visible = parentOrOuterContext.visible;
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
        return this.parser.database;
    }

    @Override
    public final Dialect dialect() {
        return this.parser.dialect;
    }


    @Override
    public final StringBuilder appendFuncName(final boolean buildIn, final String name) {
        if (!buildIn && (this.parser.isKeyWords(name) || !_DialectUtils.isSimpleIdentifier(name))) {
            String m = String.format("user defined function name[%s] for %s", name, this.parser.dialect);
            throw new CriteriaException(m);
        }
        final StringBuilder sqlBuilder = this.sqlBuilder;
        sqlBuilder.append(_Constant.SPACE);
        switch (this.parser.funcNameMode) {
            case DEFAULT:
                sqlBuilder.append(name);
                break;
            case LOWER_CASE:
                sqlBuilder.append(name.toLowerCase(Locale.ROOT));
                break;
            case UPPER_CASE:
                sqlBuilder.append(name.toUpperCase(Locale.ROOT));
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.parser.funcNameMode);
        }
        return sqlBuilder;
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
    public final void appendLiteral(final TypeMeta typeMeta, final @Nullable Object value) {
        this.parser.literal(typeMeta, value, this.sqlBuilder.append(_Constant.SPACE));

    }

    @Override
    public final void appendLiteral(final NamedLiteral namedLiteral) {

        final Function<String, Object> nameValueFunc;
        final Object value;
        if (namedLiteral == SQLs.BATCH_NO_LITERAL) {
            final IntSupplier batchNoFunc = this.paramAccepter.batchIndexFunc;
            if (batchNoFunc == null) {
                String m = String.format("%s don't support batch no literal", this.getClass().getName());
                throw new CriteriaException(m);
            }
            value = batchNoFunc.getAsInt() + 1;
        } else if ((nameValueFunc = this.paramAccepter.nameValueFunc) == null) {
            String m = String.format("%s don't support batch no literal", this.getClass().getName());
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
            sqlBuilder.append(_Constant.SPACE_NULL);
        } else if (namedLiteral instanceof SqlValueParam.SingleValue) {
            sqlBuilder.append(_Constant.SPACE);
            this.parser.literal(namedLiteral.typeMeta(), value, sqlBuilder);
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
                parser.literal(typeMeta, v, sqlBuilder);
                i++;
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        } else {
            throw _Exceptions.namedMultiParamSizeError((SqlValueParam.NamedMultiValue) namedLiteral
                    , ((Collection<?>) value).size());
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
    public final Visible visible() {
        return this.visible;
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
            this.hasNamedLiteral = true;
            if (this instanceof ParamAccepterWithOuter) {
                ((ParamAccepterWithOuter) this).outerAccepter.setHasNamedLiteral();
            }
        }

    }//ParamConsumer


    private static final class ParamAccepterWithOuter extends ParamAccepter {

        private final ParamAccepter outerAccepter;

        private ParamAccepterWithOuter(ParamAccepter outerAccepter, Function<String, Object> function,
                                       IntSupplier supplier) {
            super(outerAccepter, function, supplier);
            this.outerAccepter = outerAccepter;
        }

    }//ParamConsumerWithOuter


}
