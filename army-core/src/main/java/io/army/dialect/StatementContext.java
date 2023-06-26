package io.army.dialect;

import io.army.criteria.*;
import io.army.lang.Nullable;
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

    private final ParamConsumer paramConsumer;

    protected StatementContext(ArmyParser parser, Visible visible) {
        this(parser, false, visible);
    }

    /**
     * <p>
     * This Constructor is invoked the implementation of {@link  _ValueSyntaxInsertContext}.
     * </p>
     */
    @Deprecated
    protected StatementContext(ArmyParser dialect, boolean queryInsert, Visible visible) {
        this(null, dialect, visible);
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
                this.paramConsumer = new ParamConsumer(this::currentRowNamedValue);
            } else {
                this.paramConsumer = new ParamConsumerWithOuter(parentOrOuterContext.paramConsumer, this::currentRowNamedValue);
            }
        } else if (parentOrOuterContext instanceof MultiStmtContext && this instanceof NarrowDmlContext) {
            this.paramConsumer = new ParamConsumerWithOuter(parentOrOuterContext.paramConsumer, this::currentRowNamedValue);
        } else if (parentOrOuterContext == null) {
            this.paramConsumer = new ParamConsumer(null);
        } else {
            this.paramConsumer = parentOrOuterContext.paramConsumer;
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
        if (this instanceof _ValuesContext) {
            throw _Exceptions.valuesStatementDontSupportParam();
        }
        final ArrayList<SQLParam> paramList = this.paramConsumer.paramList;
        if (sqlParam instanceof SingleParam) {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            paramList.add(sqlParam);
        } else if (sqlParam instanceof MultiParam) {
            appendMultiParamPlaceholder(this.sqlBuilder, (SqlValueParam.MultiValue) sqlParam);
            paramList.add(sqlParam);
        } else if (sqlParam instanceof NamedParam.NamedSingle) {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            if (this.paramConsumer.function == null) {
                paramList.add(sqlParam);
                if (!this.paramConsumer.hasNamedParam) {
                    this.paramConsumer.setHasNamedParam();
                }
            } else {
                paramList.add(SingleParam.build(sqlParam.typeMeta(), readNamedValue((NamedParam) sqlParam)));
            }
        } else if (sqlParam instanceof NamedParam.NamedMulti) {
            appendMultiParamPlaceholder(this.sqlBuilder, (SqlValueParam.MultiValue) sqlParam);
            if (this.paramConsumer.function == null) {
                paramList.add(sqlParam);
                if (!this.paramConsumer.hasNamedParam) {
                    this.paramConsumer.setHasNamedParam();
                }
            } else {
                final NamedParam.NamedMulti namedMulti = (NamedParam.NamedMulti) sqlParam;
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
        if (this instanceof _ValuesContext) {
            throw new CriteriaException("Values statement don't support named literal.");
        }
        final Function<String, Object> function = this.paramConsumer.function;
        if (!(this instanceof _InsertContext) || this instanceof _QueryInsertContext || function == null) {
            String m = String.format("%s don't support %s"
                    , this.getClass().getName(), NamedLiteral.class.getName());
            throw new CriteriaException(m);
        }

        if (!this.paramConsumer.hasNamedLiteral) {
            this.paramConsumer.setHasNamedLiteral();
        }

        final Object value;
        value = function.apply(namedLiteral.name());
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
            final TypeMeta paramMeta = namedLiteral.typeMeta();
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            int i = 0;
            for (Object v : (Collection<?>) value) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    sqlBuilder.append(_Constant.SPACE);
                }
                parser.literal(paramMeta, v, sqlBuilder);//TODO codec field
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
        return this.paramConsumer.paramList.size() > 0;
    }


    @Override
    public final boolean hasNamedLiteral() {
        return this.paramConsumer.hasNamedLiteral;
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
        return _Collections.unmodifiableList(this.paramConsumer.paramList);
    }

    @Override
    public List<? extends Selection> selectionList() {
        throw new UnsupportedOperationException();
    }

    final boolean hasNamedParam() {
        return this.paramConsumer.hasNamedParam;
    }


    @Nullable
    Object currentRowNamedValue(String name) {
        String m = String.format("context[%s] don't support named value.", this.getClass().getName());
        throw new CriteriaException(m);
    }


    @Nullable
    private Object readNamedValue(final NamedParam namedParam) {
        final Function<String, Object> function = this.paramConsumer.function;
        assert function != null;
        final Object value;
        value = function.apply(namedParam.name());
        if (value == null && namedParam instanceof SqlValueParam.NonNullValue) {
            throw _Exceptions.nonNullNamedParam(namedParam);
        }
        return value;
    }

    private Collection<?> readNamedMulti(final NamedParam.NamedMulti namedParam) {
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


    private static void appendMultiParamPlaceholder(final StringBuilder sqlBuilder
            , final SqlValueParam.MultiValue sqlParam) {
        final int paramSize;
        paramSize = sqlParam.columnSize();
        assert paramSize > 0;
        for (int i = 0; i < paramSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(SPACE_PLACEHOLDER);
        }
    }


    private static class ParamConsumer {

        private final ArrayList<SQLParam> paramList;

        private final Function<String, Object> function;

        private boolean hasNamedParam;

        private boolean hasNamedLiteral;

        private ParamConsumer(@Nullable Function<String, Object> function) {
            this.paramList = _Collections.arrayList();
            this.function = function;
        }

        private ParamConsumer(ParamConsumer outerConsumer, Function<String, Object> function) {
            this.paramList = outerConsumer.paramList;
            this.hasNamedParam = outerConsumer.hasNamedParam;
            this.hasNamedLiteral = outerConsumer.hasNamedLiteral;
            this.function = function;
        }

        final void setHasNamedParam() {
            this.hasNamedParam = true;
            if (this instanceof ParamConsumerWithOuter) {
                ((ParamConsumerWithOuter) this).outerConsumer.setHasNamedParam();
            }
        }

        final void setHasNamedLiteral() {
            this.hasNamedLiteral = true;
            if (this instanceof ParamConsumerWithOuter) {
                ((ParamConsumerWithOuter) this).outerConsumer.setHasNamedLiteral();
            }
        }

    }//ParamConsumer


    private static final class ParamConsumerWithOuter extends ParamConsumer {

        private final ParamConsumer outerConsumer;

        private ParamConsumerWithOuter(ParamConsumer outerConsumer, Function<String, Object> function) {
            super(outerConsumer, function);
            this.outerConsumer = outerConsumer;
        }

    }//ParamConsumerWithOuter


}
