package io.army.criteria.impl;

import io.army.criteria.FieldSelection;
import io.army.criteria.QualifiedField;
import io.army.criteria.Selection;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.util.Objects;

abstract class Selections implements _Selection {

    static Selection forExp(_Expression expression, String alias) {
        return new ExpressionSelection((ArmyExpression) expression, alias);
    }

    static Selection forField(TableField field, String alias) {
        return new FieldSelectionImpl(field, alias);
    }

    static Selection forFunc(SQLFunctions.FunctionSpec func, String alias) {
        return new FuncSelection(func, alias);
    }


    final String alias;

    private Selections(String alias) {
        this.alias = alias;
    }


    @Override
    public final String alias() {
        return this.alias;
    }


    private static final class ExpressionSelection extends Selections {

        private final ArmyExpression expression;

        private ExpressionSelection(ArmyExpression expression, String alias) {
            super(alias);
            this.expression = expression;
        }

        @Override
        public ParamMeta paramMeta() {
            ParamMeta paramMeta = this.expression.paramMeta();
            if (paramMeta instanceof TableField) {
                // ExpressionSelection couldn't return io.army.criteria.TableField ,avoid to statement executor
                // decode selection .
                paramMeta = paramMeta.mappingType();
            }
            return paramMeta;
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            this.expression.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias, sqlBuilder);
        }

        @Override
        public TableField tableField() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return String.format(" %s AS %s", this.expression, this.alias);
        }


    }//ExpressionSelection


    private static final class FieldSelectionImpl extends Selections implements FieldSelection {

        private final TableField field;

        private FieldSelectionImpl(TableField field, String alias) {
            super(alias);
            this.field = field;
        }

        final

        @Override
        public FieldMeta<?> fieldMeta() {
            final TableField field = this.field;
            final FieldMeta<?> fieldMeta;
            if (field instanceof FieldMeta) {
                fieldMeta = (FieldMeta<?>) field;
            } else {
                fieldMeta = field.fieldMeta();
            }
            return fieldMeta;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.field;
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            ((_SelfDescribed) this.field).appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias, sqlBuilder);
        }

        @Override
        public TableField tableField() {
            return this.field;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.field, this.alias);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof FieldSelectionImpl) {
                final FieldSelectionImpl selection = (FieldSelectionImpl) obj;
                match = selection.field.equals(this.field) && selection.alias.equals(this.alias);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(_Constant.SPACE);

            final TableField field = this.field;

            if (field instanceof FieldMeta) {
                builder.append(field.columnName());
            } else if (field instanceof QualifiedField) {
                final QualifiedField<?> qualifiedField = (QualifiedField<?>) field;
                builder.append(qualifiedField.tableAlias())
                        .append(_Constant.POINT)
                        .append(field.columnName());
            } else {
                throw new IllegalStateException(String.format("field[%s] error", this.field));
            }
            return builder.append(_Constant.SPACE_AS_SPACE)
                    .append(this.alias)
                    .toString();
        }

    }//FieldSelectionImpl


    private static final class FuncSelection extends Selections {

        private final SQLFunctions.FunctionSpec func;

        private final String alias;

        private final ParamMeta returnType;

        private FuncSelection(SQLFunctions.FunctionSpec func, String alias) {
            super(alias);
            this.func = func;
            this.alias = alias;
            this.returnType = func.paramMeta();
        }

        @Override
        public ParamMeta paramMeta() {
            ParamMeta paramMeta = this.returnType;
            if (paramMeta instanceof TableField) {
                // FuncSelection couldn't return io.army.criteria.TableField ,avoid to statement executor
                // decode selection .
                paramMeta = paramMeta.mappingType();
            }
            return paramMeta;
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            this.func.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias, sqlBuilder);
        }

        @Override
        public TableField tableField() {
            //always null
            return null;
        }


    }//FuncSelection


}
