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

package io.army.criteria.impl;

import io.army.criteria.RowElement;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.criteria.impl.inner._SelectionMap;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.optional.NoCastTextType;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

abstract class SelectionGroups {

    private SelectionGroups() {
        throw new UnsupportedOperationException();
    }

    static <T> _SelectionGroup singleGroup(TableMeta<T> table, String tableAlias) {
        return new TableFieldGroupImpl<>(table, tableAlias);
    }

    static <T> _SelectionGroup groupWithoutId(ChildTableMeta<T> table, String tableAlias) {
        return new TableFieldGroupImpl<>(tableAlias, table);
    }

    /**
     * for RETURNING clause
     */
    static <T> _SelectionGroup insertTableGroup(TableMeta<T> insertTable) {
        return new InsertTableGroup<>(insertTable);
    }


    static DerivedSelectionGroup derivedGroup(_SelectionMap table, String alias) {
        return new DerivedSelectionGroup(table, alias);
    }

    /*-------------------below private method -------------------*/




    /*################################## blow static inner class  ##################################*/

    interface ObjectElementGroup extends _SelectionGroup {

        void appendObjectElement(StringBuilder sqlBuilder, _SqlContext context);

        void objectElementToString(StringBuilder builder);

    }

    interface RowElementGroup extends RowElement, _SelectionGroup {

        void appendRowElement(StringBuilder sqlBuilder, _SqlContext context);

        void rowElementToString(StringBuilder builder);


    }


    /**
     * for RETURNING clause
     * <p>
     * This class implements {@link io.army.criteria.RowExpression} for postgre row constructor.
     *
     */
    private static final class InsertTableGroup<T> implements _SelectionGroup._TableFieldGroup {

        private final TableMeta<T> insertTable;

        private InsertTableGroup(TableMeta<T> insertTable) {
            this.insertTable = insertTable;
        }


        @Override
        public String tableAlias() {
            //no bug,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {

            final List<FieldMeta<T>> fieldList;
            fieldList = this.insertTable.fieldList();

            final int fieldSize;
            fieldSize = fieldList.size();


            final DialectParser parser;
            parser = context.parser();
            FieldMeta<?> field;
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(field);
                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                parser.identifier(field.fieldName(), sqlBuilder);

            }

        }


        @Override
        public List<? extends Selection> selectionList() {
            return this.insertTable.fieldList();
        }

        @Override
        public boolean isLegalGroup(final @Nullable TableMeta<?> table) {
            return table == this.insertTable;
        }


    }//InsertTableGroup


    /**
     * <p>
     * This class implements {@link io.army.criteria.RowExpression} for postgre row constructor.
     *
     */
    static final class TableFieldGroupImpl<T> implements _SelectionGroup._TableFieldGroup, RowElementGroup,
            ObjectElementGroup {

        private final String tableAlias;

        private final List<FieldMeta<T>> fieldList;

        private TableFieldGroupImpl(TableMeta<T> table, String tableAlias) {
            this.tableAlias = tableAlias;
            this.fieldList = table.fieldList();
        }

        private TableFieldGroupImpl(String tableAlias, ChildTableMeta<T> parent) {
            final List<FieldMeta<T>> fields = parent.fieldList();
            final List<FieldMeta<T>> fieldList = _Collections.arrayList(fields.size() - 1);
            for (FieldMeta<T> field : fields) {
                if (field instanceof PrimaryFieldMeta) {
                    continue;
                }
                fieldList.add(field);
            }
            this.tableAlias = tableAlias;
            this.fieldList = Collections.unmodifiableList(fieldList);
        }


        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.fieldList;
        }

        @Override
        public boolean isLegalGroup(final @Nullable TableMeta<?> table) {
            return table != null && table == this.fieldList.get(0).tableMeta();
        }

        @Override
        public void appendRowElement(final StringBuilder slBuilder, final _SqlContext context) {
            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            assert size > 0;

            final String tableAlias = this.tableAlias;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    slBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendField(tableAlias, fieldList.get(i));
            }

        }

        @Override
        public void rowElementToString(final StringBuilder builder) {
            builder.append(_Constant.SPACE)
                    .append(this.tableAlias)
                    .append(_Constant.PERIOD)
                    .append(_Constant.ASTERISK);
        }


        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            final String tableAlias = this.tableAlias;
            final DialectParser parser;
            parser = context.parser();

            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            FieldMeta<T> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendField(tableAlias, field);

                sqlBuilder.append(_Constant.SPACE_AS_SPACE);
                parser.identifier(((TableFieldMeta<T>) field).fieldName, sqlBuilder);
            }

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            int index = 0;
            for (FieldMeta<T> field : this.fieldList) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(_Constant.SPACE)
                        .append(this.tableAlias)
                        .append(_Constant.PERIOD)
                        .append(field.columnName())
                        .append(_Constant.SPACE_AS_SPACE)
                        .append(field.fieldName());
                index++;
            }
            return builder.toString();
        }


        @Override
        public void appendObjectElement(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            assert size > 0;

            final String tableAlias = this.tableAlias;
            FieldMeta<T> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                context.appendLiteral(NoCastTextType.INSTANCE, field.columnName()); // here output column name not field name
                sqlBuilder.append(_Constant.SPACE_COMMA);
                context.appendField(tableAlias, field);
            }

        }

        @Override
        public void objectElementToString(final StringBuilder builder) {
            final List<FieldMeta<T>> fieldList = this.fieldList;
            final int size = fieldList.size();
            assert size > 0;

            final String tableAlias = this.tableAlias;
            String columnName;
            FieldMeta<T> field;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                field = fieldList.get(i);
                columnName = field.columnName();// here output column name not field name

                builder.append(_Constant.SPACE)
                        .append(_Constant.QUOTE)
                        .append(columnName)
                        .append(_Constant.QUOTE)
                        .append(_Constant.SPACE_COMMA)
                        .append(tableAlias)
                        .append(_Constant.PERIOD)
                        .append(columnName);
            }

        }


    }//TableFieldGroup


    /**
     * <p>
     * This class implements {@link io.army.criteria.RowExpression} for postgre row constructor.
     *
     */
    static final class DerivedSelectionGroup implements _SelectionGroup, RowElementGroup, ObjectElementGroup {

        private final String derivedAlias;

        private final List<? extends Selection> selectionList;

        private DerivedSelectionGroup(_SelectionMap table, String alias) {
            this.derivedAlias = alias;
            this.selectionList = table.refAllSelection();
        }

        @Override
        public void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            final DialectParser parser = context.parser();
            final String safeAlias = parser.identifier(this.derivedAlias);
            final List<? extends Selection> selectionList = this.selectionList;

            final int size = selectionList.size();
            assert size > 0;

            Selection selection;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                selection = selectionList.get(i);

                sqlBuilder.append(_Constant.SPACE)
                        .append(safeAlias)
                        .append(_Constant.PERIOD);
                parser.identifier(selection.label(), sqlBuilder);

            }
        }

        @Override
        public void appendRowElement(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw _Exceptions.castCriteriaApi();
            }
            switch (context.database()) {
                case MySQL:
                    this.appendDerivedRowElement(sqlBuilder, context);
                    break;
                case PostgreSQL: {
                    sqlBuilder.append(_Constant.SPACE);
                    context.parser().identifier(this.derivedAlias, sqlBuilder);
                    sqlBuilder.append(_Constant.PERIOD)
                            .append(_Constant.ASTERISK);
                }
                break;
                case Oracle:
                case H2:
                default:
                    throw _Exceptions.unexpectedEnum(context.database());
            }

        }

        @Override
        public void rowElementToString(final StringBuilder builder) {
            builder.append(_Constant.SPACE)
                    .append(this.derivedAlias)
                    .append(_Constant.PERIOD)
                    .append(_Constant.ASTERISK);
        }

        @Override
        public void appendObjectElement(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                throw _Exceptions.castCriteriaApi();
            }
            final DialectParser parser = context.parser();
            final String safeDerivedAlias = parser.identifier(this.derivedAlias);
            final int size = selectionList.size();
            assert size > 0;

            String selectionAlias;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                selectionAlias = selectionList.get(i).label();

                context.appendLiteral(NoCastTextType.INSTANCE, selectionAlias);
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE)
                        .append(safeDerivedAlias)
                        .append(_Constant.PERIOD);
                parser.identifier(selectionAlias, sqlBuilder);

            }

        }

        @Override
        public void objectElementToString(final StringBuilder builder) {
            final List<? extends Selection> selectionList = this.selectionList;
            if (selectionList == null) {
                builder.append(_Constant.SPACE)
                        .append(this.derivedAlias)
                        .append(_Constant.PERIOD)
                        .append(_Constant.ASTERISK);
                return;
            }

            final int size = selectionList.size();
            assert size > 0;
            final String derivedAlias = this.derivedAlias;
            String selectionAlias;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                selectionAlias = selectionList.get(i).label();

                builder.append(_Constant.SPACE)
                        .append(_Constant.QUOTE)
                        .append(selectionAlias)
                        .append(_Constant.QUOTE)
                        .append(_Constant.SPACE_COMMA_SPACE)
                        .append(derivedAlias)
                        .append(_Constant.PERIOD)
                        .append(selectionAlias);

            }

        }


        @Override
        public String tableAlias() {
            return this.derivedAlias;
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        private void appendDerivedRowElement(final StringBuilder sqlBuilder, final _SqlContext context) {
            final DialectParser dialect = context.parser();
            final String safeDerivedAlias = dialect.identifier(this.derivedAlias);
            final List<? extends Selection> selectionList = this.selectionList;
            final int size = selectionList.size();
            assert size > 0;

            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                sqlBuilder.append(_Constant.SPACE)
                        .append(safeDerivedAlias)
                        .append(_Constant.PERIOD);

                dialect.identifier(selectionList.get(i).label(), sqlBuilder);

            }
        }


    }//DerivedSelectionGroup



}
