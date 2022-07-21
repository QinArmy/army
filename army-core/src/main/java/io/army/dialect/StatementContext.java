package io.army.dialect;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.stmt.SingleParam;
import io.army.stmt.SqlParam;
import io.army.stmt.StmtParams;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
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

    /**
     * paramConsumer must be private
     */
    private final ParamConsumer paramConsumer;

    protected StatementContext(ArmyDialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        this.paramConsumer = new ParamConsumer();
    }

    /**
     * <p>
     * This Constructor is invoked the implementation of {@link  _ValueInsertContext}.
     * </p>
     */
    protected StatementContext(ArmyDialect dialect, boolean preferLiteral, Visible visible) {
        if (!(this instanceof _ValueInsertContext)) {
            throw new IllegalStateException();
        }

        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        if (preferLiteral) {
            this.paramConsumer = new ValueInsertParamConsumer(this::readNamedParam);
        } else {
            this.paramConsumer = new ParamConsumer();
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
    public final void appendParam(final SqlParam paramValue) {
        this.sqlBuilder.append(SPACE_PLACEHOLDER);
        this.paramConsumer.accept(paramValue);
    }

    @Override
    public final boolean hasParam() {
        return this.paramConsumer.paramList.size() > 0;
    }

    @Override
    public final boolean hasNamedParam() {
        return this.paramConsumer.hasNamedParam;
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


    @Nullable
    Object readNamedParam(String name) {
        throw new UnsupportedOperationException();
    }


    /**
     * This class must be private class
     */
    private static class ParamConsumer implements Consumer<SqlParam> {

        final ArrayList<SqlParam> paramList = new ArrayList<>();

        boolean hasNamedParam;

        private ParamConsumer() {

        }

        @Override
        public final void accept(final SqlParam paramValue) {
            if (paramValue instanceof NamedParam) {
                if (!this.hasNamedParam) {
                    this.hasNamedParam = true;
                }
            }

            if (!(this instanceof ValueInsertParamConsumer)) {
                this.paramList.add(paramValue);
            } else if (paramValue instanceof NamedParam) {
                ((ValueInsertParamConsumer) this).acceptNamed((NamedParam) paramValue);
            } else if (((ValueInsertParamConsumer) this).namedElementParam == null) {
                this.paramList.add(paramValue);
            } else {
                final ValueInsertParamConsumer consumer = (ValueInsertParamConsumer) this;
                throw _Exceptions.namedElementParamNotMatch(consumer.size, consumer.count);
            }

        }

    }// ParamConsumer

    private static final class ValueInsertParamConsumer extends ParamConsumer {

        private final Function<String, Object> function;

        private NamedElementParam namedElementParam;

        private int size;

        private int count;

        private ValueInsertParamConsumer(Function<String, Object> function) {
            this.function = function;
        }

        private void acceptNamed(final NamedParam namedParam) {
            final NamedElementParam namedElementParam = this.namedElementParam;
            if (namedElementParam != null) {
                if (namedParam != namedElementParam) {
                    throw _Exceptions.namedElementParamNotMatch(this.size, count);
                }
                this.count++;
                if (this.count == this.size) {
                    this.readNamedElementParam();
                } else if (this.count > this.size) {
                    //no bug,never here
                    throw new IllegalStateException("count error");
                }
            } else if (namedParam instanceof NamedElementParam) {
                final NamedElementParam elementParam = (NamedElementParam) namedParam;
                final int size = elementParam.size();
                if (size < 1) {
                    throw _Exceptions.namedElementParamSizeError(elementParam);
                }
                this.namedElementParam = (NamedElementParam) namedParam;
                this.size = size;
                this.count = 1;
                if (size == 1) {
                    this.readNamedElementParam();
                }
            } else {
                final Object value;
                value = this.function.apply(namedParam.name());
                if (value == null && namedParam instanceof NonNullNamedParam) {
                    throw _Exceptions.nonNullNamedParam((NonNullNamedParam) namedParam);
                }
                // add actual ParamValue
                this.paramList.add(SingleParam.build(namedParam.paramMeta(), value));
            }

        }


        private void readNamedElementParam() {
            final NamedElementParam elementParam = this.namedElementParam;
            assert elementParam != null;

            final int size = this.size;

            assert this.count == size;
            assert size == elementParam.size();

            final Object value;
            value = this.function.apply(elementParam.name());
            if (!(value instanceof Collection)) {
                throw _Exceptions.namedCollectionParamNotMatch(elementParam, value);
            }
            final Collection<?> collection = (Collection<?>) value;
            if (collection.size() != size) {
                throw _Exceptions.namedCollectionParamSizeError(elementParam, collection.size());
            }
            final ParamMeta paramMeta = elementParam.paramMeta();
            int elementCount = 0;
            for (Object element : collection) {
                this.paramList.add(SingleParam.build(paramMeta, element));
                elementCount++;
            }

            if (elementCount != size) {
                throw _Exceptions.namedCollectionParamSizeError(elementParam, elementCount);
            }

            //clear namedElementParam
            this.namedElementParam = null;
            this.size = 0;
            this.count = 0;
        }

    }//ValueInsertParamConsumer


}
