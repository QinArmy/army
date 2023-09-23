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

abstract class ArmySelections implements _Selection {

    static _Selection forExp(final Expression exp, final String alias) {
        final _Selection selection;
        if (exp instanceof FieldSelection) {
            if (((FieldSelection) exp).label().equals(alias)) {
                selection = (_Selection) exp;
            } else {
                selection = new FieldSelectionImpl((FieldSelection) exp, alias);
            }
        } else if (exp instanceof SQLField && ((SQLField) exp).fieldName().equals(alias)) {
            selection = (_Selection) exp;
        } else {
            selection = new ExpressionSelection((ArmyExpression) exp, alias);
        }
        return selection;
    }

    static Selection renameSelection(final Selection selection, final String alias) {
        final Selection s;
        if (selection instanceof FieldSelection) {
            if (selection.label().equals(alias)) {
                s = selection;
            } else {
                s = new FieldSelectionImpl((FieldSelection) selection, alias);
            }
        } else if (selection instanceof AnonymousSelection || !selection.label().equals(alias)) {
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
        if (typeMeta instanceof MappingType) {
            selection = new SelectionForName(alias, (MappingType) typeMeta);
        } else {
            selection = new SelectionForName(alias, typeMeta.mappingType());
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
    public final String label() {
        return this.alias;
    }

     static final class ExpressionSelection extends ArmySelections {

         final ArmyExpression expression;

         private ExpressionSelection(ArmyExpression expression, String alias) {
             super(alias);
             this.expression = expression;
         }

         @Override
         public MappingType typeMeta() {
             TypeMeta paramMeta = this.expression.typeMeta();
             if (!(paramMeta instanceof MappingType)) {
                 // ExpressionSelection couldn't return io.army.criteria.TableField ,avoid to statement executor
                 // decode selection .
                 paramMeta = paramMeta.mappingType();
             }
             return (MappingType) paramMeta;
         }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.expression.appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_AS_SPACE);

            context.identifier(this.alias, sqlBuilder);
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


     static final class FieldSelectionImpl extends ArmySelections implements FieldSelection, _SelfDescribed {

         final FieldSelection selection;

         private FieldSelectionImpl(FieldSelection selection, String alias) {
             super(alias);
             this.selection = selection;
         }

         final

        @Override
        public FieldMeta<?> fieldMeta() {
            final FieldSelection field = this.selection;
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
            return this.fieldMeta();
        }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_AS_SPACE);

            context.identifier(this.alias, sqlBuilder);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            final FieldSelection field = this.selection;
            if (field instanceof FieldMeta) {
                //here couldn't invoke appendSql() of this.field,avoid  visible field.
                context.appendField((FieldMeta<?>) this.selection);
            } else if (field instanceof QualifiedField) {
                //here couldn't invoke appendSql() of this.field,avoid  visible field.
                final QualifiedFieldImpl<?> qualifiedField = (QualifiedFieldImpl<?>) this.selection;
                context.appendField(qualifiedField.tableAlias, qualifiedField.field);
            } else {
                ((_SelfDescribed) field).appendSql(sqlBuilder, context);
            }
        }

        @Override
        public Expression underlyingExp() {
            final FieldSelection selection = this.selection;
            final Expression exp;
            if (selection instanceof TableField) {
                exp = (TableField) selection;
            } else {
                exp = ((_Selection) selection).underlyingExp();
            }
            return exp;
        }

        @Override
        public TableField tableField() {
            final FieldSelection selection = this.selection;
            final TableField field;
            if (selection instanceof TableField) {
                field = (TableField) selection;
            } else {
                field = ((_Selection) selection).tableField();
            }
            return field;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(this.selection)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(this.alias)
                    .toString();
        }

    }//FieldSelectionImpl


    static final class RenameSelection extends ArmySelections {

        final Selection selection;

        private RenameSelection(Selection selection, String alias) {
            super(alias);
            this.selection = selection;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.selection.typeMeta();
        }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
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

    private static final class SelectionForName extends ArmySelections {


        private final MappingType type;

        private SelectionForName(String alias, MappingType type) {
            super(alias);
            this.type = type;
        }


        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            //no-bug ,never here
            throw new UnsupportedOperationException();

        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }


        @Override
        public TableField tableField() {
            return null;
        }

        @Override
        public Expression underlyingExp() {
            return null;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.alias)
                    .append(_Constant.SPACE_AS_SPACE)
                    .append(this.alias)
                    .toString();
        }


    }//SelectionForName


    private static final class ColumnFuncSelection extends ArmySelections {

        private final Functions._ColumnFunction func;

        private ColumnFuncSelection(Functions._ColumnFunction func, String alias) {
            super(alias);
            this.func = func;

        }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {

            ((_SelfDescribed) this.func).appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_AS_SPACE);

            context.identifier(this.alias, sqlBuilder);
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
        public String label() {
            // no bug,never here
            throw new UnsupportedOperationException();
        }


    }//AnonymousSelectionImpl


}
