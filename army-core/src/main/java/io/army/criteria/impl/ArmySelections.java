package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.Objects;

abstract class ArmySelections implements _Selection {

    static _Selection forExp(final Expression exp, final String alias) {
        final _Selection selection;
        if (exp instanceof TableField) {
            if (((TableField) exp).fieldName().equals(alias)) {
                selection = (_Selection) exp;
            } else {
                selection = new FieldSelectionImpl((TableField) exp, alias);
            }
        } else if (exp instanceof SQLField && ((SQLField) exp).fieldName().equals(alias)) {
            selection = (_Selection) exp;
        } else {
            selection = new ExpressionSelection((ArmyExpression) exp, alias);
        }
        return selection;
    }

    static Selection renameSelection(Selection selection, String alias) {
        final Selection s;
        if (selection instanceof AnonymousSelection || !selection.alias().equals(alias)) {
            s = new RenameSelection(selection, alias);
        } else {
            s = selection;
        }
        return s;
    }


    static Selection forName(final @Nullable String alias, final @Nullable TypeMeta typeMeta) {
        if (alias == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (typeMeta == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final Selection selection;
        if (typeMeta instanceof TypeMeta.DelayTypeMeta && ((TypeMeta.DelayTypeMeta) typeMeta).isDelay()) {
            selection = new DelaySelectionForName(alias, (TypeMeta.DelayTypeMeta) typeMeta);
        } else if (typeMeta instanceof MappingType) {
            selection = new ImmutableSelectionForName(alias, (MappingType) typeMeta);
        } else {
            selection = new ImmutableSelectionForName(alias, typeMeta.mappingType());
        }
        return selection;
    }

    static Selection forAnonymous(TypeMeta type) {
        return new AnonymousSelectionImpl(type);
    }

    static Selection forColumnFunc(Functions._ColumnFunction func, String alias) {
        return new ColumnFuncSelection(func, alias);
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
        public void appendSelectItem(final _SqlContext context) {
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
        public Expression underlyingExp() {
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
        public void appendSelectItem(final _SqlContext context) {
            //here couldn't invoke appendSql() of this.field,avoid  visible field.
            if (this.field instanceof FieldMeta) {
                context.appendField((FieldMeta<?>) this.field);
            } else {
                final QualifiedFieldImpl<?> qualifiedField = (QualifiedFieldImpl<?>) this.field;
                context.appendField(qualifiedField.tableAlias, qualifiedField.field);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias, sqlBuilder);
        }

        @Override
        public Expression underlyingExp() {
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



    private static final class RenameSelection extends ArmySelections {

        private final Selection selection;

        private RenameSelection(Selection selection, String alias) {
            super(alias);
            this.selection = selection;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public void appendSelectItem(final _SqlContext context) {
            //no bug,never here
            throw new CriteriaException(String.format("%s couldn't be rendered.", RenameSelection.class.getName()));
        }

        @Override
        public TableField tableField() {
            final Selection selection = this.selection;
            final TableField field;
            if (selection instanceof _Selection) {
                field = ((_Selection) selection).tableField();
            } else {
                // probably UndoneColumnFunc
                field = null;
            }
            return field;
        }

        @Override
        public Expression underlyingExp() {
            final Selection selection = this.selection;
            final Expression expression;
            if (selection instanceof _Selection) {
                expression = ((_Selection) selection).underlyingExp();
            } else {
                // probably UndoneColumnFunc
                expression = null;
            }
            return expression;
        }


    }//RenameSelection

    private static abstract class SelectionForName extends ArmySelections {


        private SelectionForName(String alias) {
            super(alias);
        }


        @Override
        public final void appendSelectItem(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            final String safeAlias;
            safeAlias = context.parser().identifier(this.alias);

            sqlBuilder.append(safeAlias)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(safeAlias);

        }

        @Override
        public final TableField tableField() {
            return null;
        }

        @Override
        public final Expression underlyingExp() {
            return null;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.alias)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(this.alias)
                    .toString();
        }


    }//SelectionForName

    private static final class ImmutableSelectionForName extends SelectionForName {

        private final MappingType type;

        private ImmutableSelectionForName(String alias, MappingType type) {
            super(alias);
            this.type = type;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }


    }//ImmutableSelectionForName


    private static final class DelaySelectionForName extends SelectionForName implements TypeInfer.DelayTypeInfer {

        private final TypeMeta.DelayTypeMeta delay;

        private MappingType type;

        private DelaySelectionForName(String alias, TypeMeta.DelayTypeMeta delay) {
            super(alias);
            this.delay = delay;
            ContextStack.peek().addEndEventListener(this::typeMeta);
        }

        @Override
        public TypeMeta typeMeta() {
            MappingType type = this.type;
            if (type == null) {
                type = this.delay.mappingType();
                this.type = type;
            }
            return type;
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.delay.isDelay();
        }


    }//DelaySelectionForName

    private static final class ColumnFuncSelection extends ArmySelections {

        private final Functions._ColumnFunction func;

        private ColumnFuncSelection(Functions._ColumnFunction func, String alias) {
            super(alias);
            this.func = func;

        }

        @Override
        public void appendSelectItem(final _SqlContext context) {

            ((_SelfDescribed) this.func).appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias, sqlBuilder);
        }

        @Override
        public MappingType typeMeta() {
            final TypeMeta typeMeta;
            typeMeta = this.func.typeMeta();
            final MappingType type;
            if (typeMeta instanceof MappingType) {
                type = (MappingType) typeMeta;
            } else {
                type = typeMeta.mappingType();
            }
            return type;
        }


        @Override
        public TableField tableField() {
            //always null
            return null;
        }

        @Override
        public Expression underlyingExp() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.func)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(this.alias)
                    .toString();
        }


    }//ColumnFuncSelection


    private static final class AnonymousSelectionImpl implements AnonymousSelection {

        private final TypeMeta type;

        private AnonymousSelectionImpl(TypeMeta type) {
            this.type = type;
        }


        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public String alias() {
            // no bug,never here
            throw new UnsupportedOperationException();
        }


    }//AnonymousSelectionImpl


}
