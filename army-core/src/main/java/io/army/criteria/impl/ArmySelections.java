package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

import java.util.Objects;

abstract class ArmySelections implements _Selection {

    static Selection forExp(final Expression exp, final String alias) {
        final Selection selection;
        if (exp instanceof TableField) {
            if (((TableField) exp).fieldName().equals(alias)) {
                selection = (TableField) exp;
            } else {
                selection = new FieldSelectionImpl((TableField) exp, alias);
            }
        } else if (exp instanceof DataField && ((DataField) exp).fieldName().equals(alias)) {
            selection = (DataField) exp;
        } else {
            selection = new ExpressionSelection((ArmyExpression) exp, alias);
        }
        return selection;
    }

    static Selection renameSelection(Selection selection, String alias) {
        final Selection s;
        if (selection.alias().equals(alias)) {
            s = selection;
        } else {
            s = new RenameSelection(selection, alias);
        }
        return s;
    }


    static Selection forName(String alias, MappingType typeMeta) {
        return new SelectionForName(alias, typeMeta);
    }


    final String alias;

    private ArmySelections(String alias) {
        this.alias = alias;
    }


    @Override
    public final String alias() {
        return this.alias;
    }

    private static final class ExpressionSelection extends ArmySelections {

        private final ArmyExpression expression;

        private ExpressionSelection(ArmyExpression expression, String alias) {
            super(alias);
            this.expression = expression;
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta paramMeta = this.expression.typeMeta();
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
        public Expression selectionExp() {
            return this.expression;
        }

        @Override
        public String toString() {
            return String.format(" %s AS %s", this.expression, this.alias);
        }


    }//ExpressionSelection


    private static final class FieldSelectionImpl extends ArmySelections implements FieldSelection {

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
        public TypeMeta typeMeta() {
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
        public Expression selectionExp() {
            return this.field;
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


    private static final class FuncSelection extends ArmySelections {

        private final FunctionUtils.FunctionSpec func;

        private final String alias;

        private final TypeMeta returnType;

        private FuncSelection(FunctionUtils.FunctionSpec func, String alias) {
            super(alias);
            this.func = func;
            this.alias = alias;
            this.returnType = func.typeMeta();
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta paramMeta = this.returnType;
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
        public Expression selectionExp() {
            return (Expression) this.func;
        }

        @Override
        public TableField tableField() {
            //always null
            return null;
        }


    }//FuncSelection

    private static final class RenameSelection extends ArmySelections {

        private final _Selection selection;

        private RenameSelection(Selection selection, String alias) {
            super(alias);
            this.selection = (_Selection) selection;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            // here don't output selection
            context.parser().identifier(this.alias, context.sqlBuilder().append(_Constant.SPACE));
        }

        @Override
        public TableField tableField() {
            return this.selection.tableField();
        }

        @Override
        public Expression selectionExp() {
            return this.selection.selectionExp();
        }


    }//RenameSelection

    private static final class SelectionForName extends ArmySelections {

        private final MappingType typeMeta;

        private SelectionForName(String alias, MappingType typeMeta) {
            super(alias);
            this.typeMeta = typeMeta;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.typeMeta;
        }

        @Override
        public void appendSelection(final _SqlContext context) {
            context.parser().identifier(this.alias, context.sqlBuilder().append(_Constant.SPACE));
        }

        @Override
        public TableField tableField() {
            return null;
        }

        @Override
        public Expression selectionExp() {
            return null;
        }

    }//SelectionForName


}
