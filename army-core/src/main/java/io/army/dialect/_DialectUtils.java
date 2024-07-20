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

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class _DialectUtils {

    public static final BiConsumer<String, Consumer<String>> NON_BEAUTIFY_SQL_FUNC = _DialectUtils::nonBeautifySql;

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }

    public static String printDdlSqlList(final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder(128);

        for (String sql : sqlList) {
            builder.append(sql)
                    .append(_Constant.SPACE_SEMICOLON)
                    .append('\n');
        }
        return builder.toString();
    }


    /**
     * @return true : representing insert {@link ChildTableMeta} and syntax error
     * , statement executor couldn't get the auto increment primary key of {@link ParentTableMeta}
     */
    public static boolean isIllegalChildPostInsert(final _Insert._ChildInsert stmt) {
        final _Insert parentStmt;
        parentStmt = stmt.parentStmt();

        final boolean needReturnId, cannotReturnId;
        needReturnId = !(parentStmt instanceof _Insert._QueryInsert || ((_Insert._InsertOption) parentStmt).isMigration())
                && parentStmt.table().id().generatorType() == GeneratorType.POST;

        cannotReturnId = needReturnId
                && parentStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) parentStmt).hasConflictAction()
                && parentStmt.insertRowCount() > 1
                && (!(parentStmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) parentStmt).isIgnorableConflict());
        return needReturnId && cannotReturnId;
    }

    /**
     * @deprecated validate by statement executor.
     */
    @Deprecated
    public static boolean isIgnorableConflict(final _Insert._ChildInsert childStmt) {
        final _Insert parentStmt = childStmt.parentStmt();
        final boolean parentIgnorable, childIgnorable;
        parentIgnorable = parentStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) parentStmt).isIgnorableConflict();

        childIgnorable = childStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) childStmt).isIgnorableConflict();
        //here, validate do nothing only,rest is validate by statement executor.
        return parentIgnorable || childIgnorable;
    }

    public static boolean isIllegalTwoStmtMode(final _Insert._ChildInsert childStmt) {
        return !(childStmt instanceof _ReturningDml) && childStmt.parentStmt() instanceof _ReturningDml;
    }

    /**
     * @param stmt non {@link io.army.criteria.impl.inner._Insert._ChildInsert}
     */
    public static boolean isCannotReturnId(final _Insert._DomainInsert stmt) {
        final TableMeta<?> table = stmt.table();
        if (table instanceof ChildTableMeta) {
            //here,stmt not tow statement mode,for example postgre insert with cte.
            return false;
        }
        final boolean needReturnId, cannotReturnId;
        needReturnId = stmt instanceof PrimaryStatement
                && !stmt.isMigration()
                && table.id().generatorType() == GeneratorType.POST
                && !stmt.isIgnoreReturnIds();

        cannotReturnId = needReturnId
                && stmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) stmt).hasConflictAction()
                && stmt.insertRowCount() > 1
                && (!(stmt instanceof _Statement._ReturningListSpec) || ((_Insert._SupportConflictClauseSpec) stmt).isIgnorableConflict());
        return needReturnId && cannotReturnId;

    }


    /**
     * @return a unmodified list
     */
    public static List<_Selection> flatSelectItem(final List<? extends SelectItem> selectItemList) {
        final int itemSize;
        itemSize = selectItemList.size();
        final List<_Selection> selectionList = _Collections.arrayList(itemSize);

        List<? extends Selection> selectionListOfGroup;

        SelectItem selectItem;
        for (int i = 0, groupSize; i < itemSize; i++) {
            selectItem = selectItemList.get(i);
            if (selectItem instanceof Selection) {
                selectionList.add((_Selection) selectItem);
            } else if (selectItem instanceof _SelectionGroup) {
                selectionListOfGroup = ((_SelectionGroup) selectItem).selectionList();
                groupSize = selectionListOfGroup.size();
                for (int j = 0; j < groupSize; j++) {
                    selectionList.add((_Selection) selectionListOfGroup.get(j));
                }
            } else {
                throw _Exceptions.unknownSelectItem(selectItem);
            }
        }
        return _Collections.unmodifiableList(selectionList);
    }


    /**
     * @see ArmyParser#safeObjectName(DatabaseObject)
     */
    public static boolean isSimpleIdentifier(final String objectName) {
        final int length = objectName.length();
        char ch;
        // empty string isn't safe identifier
        boolean match = length > 0;
        for (int i = 0; i < length; i++) {
            ch = objectName.charAt(i);
            if ((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || ch == '_') {
                continue;
            } else if (i > 0 && (ch >= '0' && ch <= '9')) {
                continue;
            }
            match = false;
            break;
        }
        return match;
    }


    public static void checkInsertField(final TableMeta<?> table, final FieldMeta<?> field
            , final @Nullable BiFunction<FieldMeta<?>, Function<FieldMeta<?>, CriteriaException>, CriteriaException> function) {

        if (!field.insertable()) {
            if (function == null) {
                throw _Exceptions.nonInsertableField(field);
            }
            throw function.apply(field, _Exceptions::nonInsertableField);
        }
        if (field.tableMeta() != table) {
            if (function == null) {
                throw _Exceptions.unknownColumn(null, field);
            }
            throw function.apply(field, _Exceptions::unknownColumn);
        }
        if (field == table.discriminator()) {
            if (function == null) {
                throw _Exceptions.armyManageField(field);
            }
            throw function.apply(field, _Exceptions::armyManageField);
        }

        switch (field.fieldName()) {
            case _MetaBridge.ID:
            case _MetaBridge.CREATE_TIME:
            case _MetaBridge.UPDATE_TIME:
            case _MetaBridge.VERSION: {
                if (function == null) {
                    throw _Exceptions.armyManageField(field);
                }
                throw function.apply(field, _Exceptions::armyManageField);
            }
            default:
                //no-op
        }

        if (field.generatorType() != null) {
            if (function == null) {
                throw _Exceptions.insertExpDontSupportField(field);
            }
            throw function.apply(field, _Exceptions::insertExpDontSupportField);
        }


    }


    /*################################## blow package method ##################################*/


    static void validateTableAlias(final String tableAlias) {
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(_Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, _Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }


    static boolean hasOptimistic(List<_Predicate> predicateList) {
        boolean match = false;
        for (_Predicate predicate : predicateList) {
            if (predicate.isOptimistic()) {
                match = true;
                break;
            }
        }
        return match;
    }

    static void appendConditionFields(final _SingleUpdateContext context
            , final @Nullable List<? extends TableField> conditionFieldList) {
        if (conditionFieldList == null || conditionFieldList.size() == 0) {
            return;
        }
        final String safeTableAlias = context.safeTargetTableAlias();
        final ArmyParser dialect = (ArmyParser) context.parser();
        final StringBuilder sqlBuilder = context.sqlBuilder();

        String safeColumnName;
        for (TableField field : conditionFieldList) {
            sqlBuilder.append(_Constant.SPACE_AND_SPACE)
                    .append(safeTableAlias)
                    .append(_Constant.PERIOD);

            safeColumnName = dialect.safeObjectName(field);
            sqlBuilder.append(safeColumnName);
            switch (field.updateMode()) {
                case ONLY_NULL:
                    sqlBuilder.append(_Constant.SPACE_IS_NULL);
                    break;
                case ONLY_DEFAULT: {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(dialect.defaultFuncName())
                            .append(_Constant.LEFT_PAREN)
                            .append(_Constant.SPACE)
                            .append(safeTableAlias)
                            .append(_Constant.PERIOD)
                            .append(safeColumnName)
                            .append(_Constant.SPACE_RIGHT_PAREN);

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(field.updateMode());

            }

        }
    }

    static boolean isIllegalConflict(final _Insert stmt, final Visible visible) {
        if (visible == Visible.BOTH || !stmt.table().containComplexField(_MetaBridge.VISIBLE)) {
            return false;
        }

        final _Insert nonChildStmt;
        if (stmt instanceof _Insert._ChildInsert) {
            nonChildStmt = ((_Insert._ChildInsert) stmt).parentStmt();
        } else {
            nonChildStmt = stmt;
        }
        final boolean nonChildIllegal, childIllegal;
        nonChildIllegal = nonChildStmt instanceof _Insert._SupportConflictClauseSpec
                && ((_Insert._SupportConflictClauseSpec) nonChildStmt).hasConflictAction()
                && !((_Insert._SupportConflictClauseSpec) nonChildStmt).isIgnorableConflict();

        if (nonChildStmt == stmt) {
            childIllegal = false;
        } else {
            childIllegal = stmt instanceof _Insert._SupportConflictClauseSpec
                    && ((_Insert._SupportConflictClauseSpec) stmt).hasConflictAction()
                    && !((_Insert._SupportConflictClauseSpec) stmt).isIgnorableConflict();
        }

        return nonChildIllegal || childIllegal;
    }

    static void checkDefaultValueMap(final _Insert._ValuesSyntaxInsert insert) {
        if (insert.isMigration()) {
            return;
        }
        final TableMeta<?> table = insert.table();

        FieldMeta<?> field;
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.defaultValueMap().entrySet()) {
            field = e.getKey();
            checkInsertField(table, field, null);
            if (field.notNull() && e.getValue().isNullValue()) {
                throw _Exceptions.nonNullField(field);
            }

        }

    }

    /**
     * @return a unmodified map
     */
    static Map<String, Boolean> createKeyWordMap(final Set<String> keyWordSet) {
        final Map<String, Boolean> map;
        map = _Collections.hashMap((int) (keyWordSet.size() / 0.75f));
        for (String keyWord : keyWordSet) {
            map.putIfAbsent(keyWord.toUpperCase(Locale.ROOT), Boolean.TRUE);
        }

        for (String keyWord : fieldCoreKeyWordMap().keySet()) {
            map.putIfAbsent(keyWord.toUpperCase(Locale.ROOT), Boolean.TRUE);
        }
        return Collections.unmodifiableMap(map);
    }


    static int generatedFieldSize(final TableMeta<?> domainTable, final boolean manageVisible) {
        int size = 1; //create time

        if (domainTable instanceof SingleTableMeta) {
            if (domainTable.containField(_MetaBridge.UPDATE_TIME)) {
                size++;
            }
            if (domainTable.containField(_MetaBridge.VERSION)) {
                size++;
            }
            if (manageVisible && domainTable.containField(_MetaBridge.VISIBLE)) {
                size++;
            }
        }

        if (!(domainTable instanceof SimpleTableMeta)) {
            size++; //discriminator
        }


        size += domainTable.fieldChain().size();

        if (domainTable instanceof ChildTableMeta) {
            size += ((ChildTableMeta<?>) domainTable).parentMeta().fieldChain().size();
        }
        return size;
    }


    @Nullable
    static Object readParamValue(final FieldMeta<?> field, final @Nullable _Expression expression
            , final MappingEnv mappingEnv) {
        if (!(expression instanceof SqlValueParam.SingleAnonymousValue)) {
            return null;
        }
        Object value;
        value = ((SqlValueParam.SingleAnonymousValue) expression).value();

        final Class<?> javaType = field.javaType();
        if (value == null || javaType.isInstance(value)) {
            return value;
        }
        value = field.mappingType().convert(mappingEnv, value);
        if (!javaType.isInstance(value)) {
            String m = String.format("%s convert method don't return instance of %s"
                    , field.mappingType().getClass().getName(), javaType.getName());
            throw new MetaException(m);
        }
        return value;

    }




    /*-------------------below private method -------------------*/


    /**
     * @see #createKeyWordMap(Set)
     */
    private static Map<String, Boolean> fieldCoreKeyWordMap() {
        final Map<String, Boolean> map;
        map = _Collections.hashMap();

        map.put("SELECT", Boolean.TRUE);
        map.put("WITH", Boolean.TRUE);
        map.put("RECURSIVE", Boolean.TRUE);
        map.put("COMMENT", Boolean.TRUE);

        map.put("INSERT", Boolean.TRUE);
        map.put("INTO", Boolean.TRUE);
        map.put("VALUES", Boolean.TRUE);

        map.put("UPDATE", Boolean.TRUE);
        map.put("FROM", Boolean.TRUE);
        map.put("DELETE", Boolean.TRUE);
        map.put("SET", Boolean.TRUE);

        map.put("WHERE", Boolean.TRUE);
        map.put("AND", Boolean.TRUE);

        return map;
    }




    /*################################## blow private static innner class ##################################*/

    private static void nonBeautifySql(String sql, Consumer<String> appender) {
        appender.accept(sql);
    }


}
