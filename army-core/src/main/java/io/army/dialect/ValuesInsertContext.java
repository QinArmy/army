package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.InsertStmtParams;
import io.army.stmt.SingleParam;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements InsertStmtParams {


    static ValuesInsertContext forSingle(@Nullable _SqlContext outerContext, _Insert._ValuesInsert stmt
            , ArmyParser dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(stmt);
        return new ValuesInsertContext((StatementContext) outerContext, stmt, dialect, visible);
    }

    static ValuesInsertContext forParent(@Nullable _SqlContext outerContext, _Insert._ChildValuesInsert domainStmt
            , ArmyParser dialect, Visible visible) {

        return new ValuesInsertContext((StatementContext) outerContext, domainStmt, dialect, visible);
    }

    static ValuesInsertContext forChild(@Nullable _SqlContext outerContext, _Insert._ChildValuesInsert insert
            , ValuesInsertContext parentContext) {
        return new ValuesInsertContext((StatementContext) outerContext, insert, parentContext);
    }


    private final List<Map<FieldMeta<?>, _Expression>> rowList;

    private final ValuesRowWrapper rowWrapper;

    private final List<Map<FieldMeta<?>, Object>> generatedValuesList;

    private List<Map<FieldMeta<?>, Object>> tempGeneratedValuesList;

    private int currentBatchIndex;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_SqlContext, _Insert._ValuesInsert, ArmyParser, Visible)
     * @see #forParent(_SqlContext, _Insert._ChildValuesInsert, ArmyParser, Visible)
     */
    private ValuesInsertContext(@Nullable StatementContext outerContext, _Insert._ValuesInsert domainStmt
            , ArmyParser parser, Visible visible) {
        super(outerContext, domainStmt, parser, visible);

        if (domainStmt instanceof _Insert._ChildValuesInsert) {
            this.rowList = ((_Insert._ChildValuesInsert) domainStmt).parentStmt().rowPairList();
        } else {
            this.rowList = domainStmt.rowPairList();
        }

        final int rowSize = this.rowList.size();
        assert rowSize > 0;

        if (this.migration) {
            this.generatedValuesList = null;
        } else {
            final List<Map<FieldMeta<?>, Object>> list = new ArrayList<>(rowSize);
            this.generatedValuesList = Collections.unmodifiableList(list);
            this.tempGeneratedValuesList = list;
        }
        this.rowWrapper = new ValuesRowWrapper(this, domainStmt);  //must be domainStmt


    }

    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     *
     * @see #forChild(_SqlContext, _Insert._ChildValuesInsert, ValuesInsertContext)
     */
    private ValuesInsertContext(@Nullable StatementContext outerContext, _Insert._ChildValuesInsert stmt,
                                ValuesInsertContext parentContext) {
        super(outerContext, stmt, parentContext);

        this.rowList = stmt.rowPairList();
        assert this.rowList.size() == parentContext.rowList.size();

        assert this.rowList != parentContext.rowList;
        this.generatedValuesList = parentContext.generatedValuesList;
        this.tempGeneratedValuesList = null;
        this.rowWrapper = parentContext.rowWrapper;

        assert this.rowWrapper.nonChildRowList == parentContext.rowList;

    }


    @Override
    int doAppendValuesList(final int outputColumnSize, final List<FieldMeta<?>> fieldList) {

        final List<Map<FieldMeta<?>, _Expression>> rowValuesList = this.rowList;
        final int rowSize = rowValuesList.size();
        final int fieldSize = fieldList.size();

        final ArmyParser parser = this.parser;
        final boolean migration = this.migration;
        final NullMode nullHandleMode = this.nullMode;
        final LiteralMode literalMode = this.literalMode;

        final boolean mockEnv = parser.mockEnv;
        final boolean twoStmtMode = this.twoStmtMode;
        final ValuesRowWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> insertTable = this.insertTable, domainTable = rowWrapper.domainTable;


        final FieldValueGenerator generator;
        final List<Map<FieldMeta<?>, Object>> generatedValuesList;


        final FieldMeta<?> discriminator = domainTable.discriminator();
        final String spaceDiscriminator;
        if (domainTable instanceof ChildTableMeta) {
            spaceDiscriminator = _Constant.SPACE + Integer.toString(domainTable.discriminatorValue().code());
        } else {
            spaceDiscriminator = _Constant.SPACE_ZERO;
        }
        final List<Object> postIdList = rowWrapper.postIdList;
        final boolean manageVisible;
        final int generatedFieldSize;
        final Map<FieldMeta<?>, _Expression> defaultValueMap;
        if (insertTable instanceof ChildTableMeta) {
            generator = null;
            generatedValuesList = this.generatedValuesList;
            manageVisible = false;
            assert !(generatedValuesList == null && !migration);

            generatedFieldSize = 0;
            if (twoStmtMode) {
                defaultValueMap = rowWrapper.childDefaultMap;
            } else {
                defaultValueMap = rowWrapper.nonChildDefaultMap;
            }
        } else {
            final FieldMeta<?> visibleField = insertTable.tryGetField(_MetaBridge.VISIBLE);
            manageVisible = visibleField != null && !rowWrapper.nonChildDefaultMap.containsKey(visibleField);
            generator = parser.generator;
            generatedValuesList = this.tempGeneratedValuesList;
            if (generatedValuesList == null) {
                assert migration;
            } else {
                assert generatedValuesList instanceof ArrayList;
                this.tempGeneratedValuesList = null;
            }
            generatedFieldSize = (int) (_DialectUtils.generatedFieldSize(domainTable, manageVisible) / 0.75F);
            defaultValueMap = rowWrapper.nonChildDefaultMap;
        }


        final Map<FieldMeta<?>, Object> emptyMap = Collections.emptyMap();
        final PrimaryFieldMeta<?> nonChildId = insertTable.nonChildId();
        final GeneratorType nonChildIdType = nonChildId.generatorType();
        final boolean postParentId = nonChildIdType == GeneratorType.POST;

        Map<FieldMeta<?>, _Expression> rowValuesMap;
        Map<FieldMeta<?>, Object> generatedMap;
        DelayIdParamValue delayIdParam;
        _Expression expression;
        FieldMeta<?> field;
        Object value;

        int outputValueSize = 0;

        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder.append(_Constant.SPACE_VALUES);

        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            this.currentBatchIndex = rowIndex;

            rowValuesMap = rowValuesList.get(rowIndex);
            rowWrapper.rowValuesMap = rowValuesMap;

            if (generator == null) {//here insertTable is ChildTable
                generatedMap = generatedValuesList == null ? emptyMap : generatedValuesList.get(rowIndex);
                rowWrapper.generatedMap = null;
            } else if (migration) {
                rowWrapper.generatedMap = generatedMap = emptyMap;
                //use ths.domainTable,not this.insertTable
                generator.validate(domainTable, rowWrapper);// validate the values that is managed by army
            } else {
                generatedMap = new HashMap<>(generatedFieldSize);
                rowWrapper.generatedMap = generatedMap; // update domain value
                //use ths.domainTable,not this.insertTable
                generator.generate(domainTable, manageVisible, rowWrapper); // create the values that is managed by army
                generatedValuesList.add(generatedMap);
            }

            if (rowIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

            delayIdParam = null; //clear last param
            assert outputValueSize == 0 || outputValueSize == outputColumnSize;
            outputValueSize = -1;//reset
            for (int fieldIndex = 0, actualFieldIndex = 0; fieldIndex < fieldSize; fieldIndex++) {
                field = fieldList.get(fieldIndex);
                if (!migration && !field.insertable()) {
                    // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                    continue;
                }
                if (actualFieldIndex > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                actualFieldIndex++;
                outputValueSize = actualFieldIndex;

                if (field == discriminator) {
                    assert insertTable instanceof ParentTableMeta;
                    sqlBuilder.append(spaceDiscriminator);
                } else if (postParentId && field instanceof PrimaryFieldMeta && insertTable instanceof ChildTableMeta) {
                    if (twoStmtMode) {
                        assert delayIdParam == null && postIdList != null;
                        delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, rowIndex, postIdList::get);
                        this.appendParam(delayIdParam);
                    } else if ((expression = defaultValueMap.get(field)) == null) {
                        throw this.oneStmtModePostChildNoIdExpression();
                    } else {
                        expression.appendSql(sqlBuilder, this);
                    }
                } else if ((value = generatedMap.get(field)) != null) { // read the value that is generated by army
                    this.appendInsertValue(literalMode, field, value);
                } else if (field instanceof PrimaryFieldMeta) { //child id must be managed by army
                    if (migration || nonChildIdType == null) {
                        expression = rowWrapper.nonChildRowList.get(rowIndex).get(nonChildId);
                        assert expression instanceof SqlValueParam.SingleAnonymousValue;//because io.army.dialect.FieldValueGenerator have validated
                        expression.appendSql(sqlBuilder, this);
                    } else {
                        assert mockEnv;//must assert
                        this.appendInsertValue(literalMode, field, null);
                    }
                } else if ((expression = rowValuesMap.get(field)) != null
                        || (expression = defaultValueMap.get(field)) != null) {
                    expression.appendSql(sqlBuilder, this);
                } else if (field.generatorType() == GeneratorType.PRECEDE) {
                    assert mockEnv || migration;
                    if (migration && !field.nullable()) {
                        throw _Exceptions.nonNullField(field);
                    }
                    this.appendInsertValue(literalMode, field, null);
                } else if (nullHandleMode == NullMode.INSERT_DEFAULT) {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                } else if (nullHandleMode != NullMode.INSERT_NULL) {
                    //no bug,never here
                    throw _Exceptions.unexpectedEnum(nullHandleMode);
                } else if (!field.nullable()) {
                    throw _Exceptions.nonNullField(field);
                } else {
                    this.appendInsertValue(literalMode, field, null);
                }

            }//inner for
            assert outputValueSize == outputColumnSize; //assert value size and column size match.
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }//outer for


        rowWrapper.rowValuesMap = null; //finally must clear
        rowWrapper.generatedMap = null;//finally must clear

        return outputValueSize;

    }


    @Override
    public int rowSize() {
        return this.rowList.size();
    }


    @Override
    public ObjIntConsumer<Object> idConsumer() {
        final List<Object> postIdList = this.rowWrapper.postIdList;
        assert postIdList != null && this.isValuesClauseEnd() && this.insertTable instanceof SingleTableMeta;
        final int rowSize = this.rowWrapper.nonChildRowList.size();
        final PrimaryFieldMeta<?> idField = this.returnId;
        assert idField != null;
        return (idValue, indexBasedZero) -> {
            if (idValue == null) {
                //no bug,never here
                throw new NullPointerException();
            }
            final int currentSize = postIdList.size();
            if (indexBasedZero == currentSize) {
                postIdList.add(idValue);
            } else if (indexBasedZero < 0 || indexBasedZero > currentSize || indexBasedZero > rowSize) {
                //no bug,never here
                throw new IllegalArgumentException();
            } else {
                throw _Exceptions.duplicateIdValue(indexBasedZero, idField, idValue);
            }


        };

    }


    @Override
    Object readCurrentRowNamedValue(final String name) {
        final ValuesRowWrapper wrapper = this.rowWrapper;
        final TableMeta<?> domainTable = wrapper.domainTable;
        final FieldMeta<?> field;
        field = domainTable.tryGetComplexFiled(name);
        if (field == null) {
            throw _Exceptions.invalidNamedParam(name);
        }
        final Map<FieldMeta<?>, Object> generatedMap = wrapper.generatedMap;
        assert generatedMap != null;
        return generatedMap.get(field);

    }

    @Override
    int readCurrentBatchIndex() {
        return this.currentBatchIndex;
    }

    private CriteriaException oneStmtModePostChildNoIdExpression() {
        String m = String.format("error,you use %s one statement mode values insert %s,but no child id default expression",
                this.parser.database.name(), this.insertTable);
        return new CriteriaException(m);
    }

    private static final class ValuesRowWrapper extends _DialectUtils.ExpRowWrapper {

        private final List<Map<FieldMeta<?>, _Expression>> nonChildRowList;

        private final Map<FieldMeta<?>, _Expression> nonChildDefaultMap;

        private final Map<FieldMeta<?>, _Expression> childDefaultMap;

        private final List<Object> postIdList;

        private Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        private ValuesRowWrapper(ValuesInsertContext context, _Insert._ValuesInsert domainStmt) {
            super(domainStmt.table(), context.parser.mappingEnv);
            this.nonChildRowList = context.rowList;
            if (domainStmt instanceof _Insert._ChildValuesInsert) {
                assert context.insertTable == ((ChildTableMeta<?>) this.domainTable).parentMeta();
                final _Insert._ValuesInsert parentStmt = ((_Insert._ChildValuesInsert) domainStmt).parentStmt();
                this.nonChildDefaultMap = parentStmt.defaultValueMap();
                this.childDefaultMap = domainStmt.defaultValueMap();
            } else {
                assert context.insertTable == this.domainTable;
                this.nonChildDefaultMap = domainStmt.defaultValueMap();
                this.childDefaultMap = Collections.emptyMap();
            }

            if (context.returnId == null) {
                postIdList = null;
            } else {
                postIdList = _Collections.arrayList(this.nonChildRowList.size());
            }

        }

        @Override
        public void set(final FieldMeta<?> field, final @Nullable Object value) {
            final Map<FieldMeta<?>, Object> map = this.generatedMap;
            assert map != null;
            if (value == null) {
                //here mock environment
                return;
            }
            map.put(field, value);

            if (field instanceof PrimaryFieldMeta) {
                final TableMeta<?> fieldTable = field.tableMeta();
                assert fieldTable instanceof SingleTableMeta;
                if (fieldTable != this.domainTable) {
                    map.put(this.domainTable.id(), value);
                }
            }

        }

        @Override
        Object getGeneratedValue(FieldMeta<?> field) {
            final Map<FieldMeta<?>, Object> map = this.generatedMap;
            assert map != null;
            return map.get(field);
        }

        @Override
        _Expression getExpression(final FieldMeta<?> field) {
            final Map<FieldMeta<?>, _Expression> map = this.rowValuesMap;
            assert map != null;
            _Expression expression;
            expression = map.get(field);
            if (expression != null) {
                return expression;
            }
            if (field.tableMeta() instanceof ChildTableMeta) {
                expression = this.childDefaultMap.get(field);
            } else {
                expression = this.nonChildDefaultMap.get(field);
            }
            return expression;
        }

    }//ValuesRowWrapper


    private static final class DelayIdParamValue implements SingleParam {

        private final PrimaryFieldMeta<?> field;

        private final int rowIndex;

        private final IntFunction<Object> function;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, int rowIndex, IntFunction<Object> function) {
            this.field = field;
            this.rowIndex = rowIndex;
            this.function = function;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.field;
        }

        @Override
        public Object value() {
            final Object value;
            value = this.function.apply(this.rowIndex);
            if (value == null) {
                //no bug,never here
                throw parentStmtDontExecute(this.field);
            }
            return value;
        }


    }//DelayIdParamValue


}
