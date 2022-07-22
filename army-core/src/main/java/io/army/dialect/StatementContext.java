package io.army.dialect;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.stmt.MultiParam;
import io.army.stmt.SingleParam;
import io.army.stmt.StmtParams;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * This class is base class of all the implementation of {@link  _SqlContext}.
 * </p>
 *
 * @since 1.0
 */
abstract class StatementContext implements StmtContext, StmtParams {

    static final String SPACE_PLACEHOLDER = " ?";

    protected final ArmyDialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    private final ParamConsumer paramConsumer;

    protected StatementContext(ArmyDialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        this.paramConsumer = new ParamConsumer(null);
    }

    /**
     * <p>
     * This Constructor is invoked the implementation of {@link  _ValueInsertContext}.
     * </p>
     */
    protected StatementContext(ArmyDialect dialect, boolean nonQueryInsert, Visible visible) {
        if (!(this instanceof InsertContext) || this instanceof _QueryInsertContext) {
            throw new IllegalStateException();
        }

        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        if (nonQueryInsert) {
            this.paramConsumer = new ParamConsumer(this::currentRowNamedValue);
        } else {
            this.paramConsumer = new ParamConsumer(null);
        }
    }

    protected StatementContext(StatementContext outerContext) {
        this.dialect = outerContext.dialect;
        this.visible = outerContext.visible;
        this.sqlBuilder = outerContext.sqlBuilder;
        this.paramConsumer = outerContext.paramConsumer;

    }


    @Override
    public final DialectParser dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }


    @Override
    public final void appendParam(final SqlParam sqlParam) {
        final ArrayList<SqlParam> paramList = this.paramConsumer.paramList;
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
                this.paramConsumer.hasNamedParam = true;
            } else {
                paramList.add(SingleParam.build(sqlParam.paramMeta(), readNamedValue((NamedParam) sqlParam)));
            }
        } else if (sqlParam instanceof NamedParam.NamedMulti) {
            appendMultiParamPlaceholder(this.sqlBuilder, (SqlValueParam.MultiValue) sqlParam);
            if (this.paramConsumer.function == null) {
                paramList.add(sqlParam);
                this.paramConsumer.hasNamedParam = true;
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
    public final void appendLiteral(final NamedLiteral namedLiteral) {
        final Function<String, Object> function = this.paramConsumer.function;
        if (!(this instanceof InsertContext) || this instanceof _QueryInsertContext || function == null) {
            String m = String.format("%s don't support %s"
                    , this.getClass().getName(), NamedLiteral.class.getName());
            throw new CriteriaException(m);
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
            this.dialect.literal(namedLiteral.paramMeta(), value, sqlBuilder);
        } else if (!(namedLiteral instanceof SqlValueParam.NamedMultiValue)) {
            //no bug,never here
            throw new IllegalArgumentException();
        } else if (!(value instanceof Collection)) {
            throw _Exceptions.namedParamNotMatch((SqlValueParam.NamedMultiValue) namedLiteral, value);
        } else if (((Collection<?>) value).size() == ((SqlValueParam.NamedMultiValue) namedLiteral).valueSize()) {

            final ArmyDialect dialect = this.dialect;
            final ParamMeta paramMeta = namedLiteral.paramMeta();
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            int i = 0;
            for (Object v : (Collection<?>) value) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    sqlBuilder.append(_Constant.SPACE);
                }
                dialect.literal(paramMeta, v, sqlBuilder);//TODO codec field
                i++;
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        } else {
            throw _Exceptions.namedMultiParamSizeError((SqlValueParam.NamedMultiValue) namedLiteral
                    , ((Collection<?>) value).size());
        }

    }

    @Override
    public final boolean hasParam() {
        return this.paramConsumer.paramList.size() > 0;
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
    public final List<SqlParam> paramList() {
        return _CollectionUtils.unmodifiableList(this.paramConsumer.paramList);
    }

    @Override
    public List<Selection> selectionList() {
        throw new UnsupportedOperationException();
    }

    final boolean hasNamedParam() {
        return this.paramConsumer.hasNamedParam;
    }


    @Nullable
    Object currentRowNamedValue(String name) {
        throw new UnsupportedOperationException();
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


    private static void appendMultiParamPlaceholder(final StringBuilder sqlBuilder
            , final SqlValueParam.MultiValue sqlParam) {
        final int paramSize;
        paramSize = sqlParam.valueSize();
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


    private static final class ParamConsumer {

        private final ArrayList<SqlParam> paramList = new ArrayList<>();

        private final Function<String, Object> function;

        private boolean hasNamedParam;

        private ParamConsumer(@Nullable Function<String, Object> function) {
            this.function = function;
        }

    }


}
