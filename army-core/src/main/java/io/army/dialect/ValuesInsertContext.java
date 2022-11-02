package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.LiteralMode;
import io.army.criteria.NullMode;
import io.army.criteria.SqlValueParam;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmts;
import io.army.stmt._InsertStmtParams;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

final class ValuesInsertContext extends ValuesSyntaxInsertContext implements _InsertStmtParams._ValueParams {


    static ValuesInsertContext forSingle(_Insert._ValuesInsert stmt, ArmyParser dialect, Visible visible) {
        _DialectUtils.checkDefaultValueMap(stmt);
        return new ValuesInsertContext(stmt, dialect, visible);
    }

    static ValuesInsertContext forParent(_Insert._ChildValuesInsert domainStmt, ArmyParser dialect, Visible visible) {
        return new ValuesInsertContext(domainStmt, dialect, visible);
    }

    static ValuesInsertContext forChild(ValuesInsertContext parentContext, _Insert._ChildValuesInsert insert
            , ArmyParser dialect, Visible visible) {
        return new ValuesInsertContext(parentContext, insert, dialect, visible);
    }


    private final List<Map<FieldMeta<?>, _Expression>> rowList;

    private final ValuesRowWrapper rowWrapper;

    private final List<Map<FieldMeta<?>, Object>> generatedValuesList;

    private List<Map<FieldMeta<?>, Object>> tempGeneratedValuesList;

    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     *
     * @see #forSingle(_Insert._ValuesInsert, ArmyParser, Visible)
     * @see #forParent(_Insert._ChildValuesInsert, ArmyParser, Visible)
     */
    private ValuesInsertContext(_Insert._ValuesInsert domainStmt, ArmyParser dialect, Visible visible) {
        super(dialect, domainStmt, visible);

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
     * @see #forChild(ValuesInsertContext, _Insert._ChildValuesInsert, ArmyParser, Visible)
     */
    private ValuesInsertContext(ValuesInsertContext parentContext, _Insert._ChildValuesInsert stmt
            , ArmyParser dialect, Visible visible) {
        super(parentContext, stmt, dialect, visible);

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

        final ArmyParser dialect = this.parser;
        final Map<FieldMeta<?>, _Expression> defaultValueMap;

        final boolean migration = this.migration;
        final NullMode nullHandleMode = this.nullMode;
        final LiteralMode literalMode = this.literalMode;
        final boolean mockEnv = dialect.isMockEnv();

        final FieldValueGenerator generator;
        final ValuesRowWrapper rowWrapper = this.rowWrapper;
        final TableMeta<?> insertTable = this.insertTable, domainTable = rowWrapper.domainTable;
        final List<Map<FieldMeta<?>, Object>> generatedValuesList;


        final FieldMeta<?> discriminator = domainTable.discriminator();
        final String spaceDiscriminator;
        if (domainTable instanceof ChildTableMeta) {
            spaceDiscriminator = _Constant.SPACE + Integer.toString(domainTable.discriminatorValue().code());
        } else {
            spaceDiscriminator = _Constant.SPACE_ZERO;
        }
        final Map<Integer, Object> postIdMap = rowWrapper.postIdMap;
        final boolean manageVisible;
        final int generatedFieldSize;

        if (insertTable instanceof ChildTableMeta) {
            generator = null;
            generatedValuesList = this.generatedValuesList;
            manageVisible = false;
            assert !(generatedValuesList == null && !migration);

            generatedFieldSize = 0;
            defaultValueMap = rowWrapper.childDefaultMap;
        } else {
            final FieldMeta<?> visibleField = insertTable.tryGetField(_MetaBridge.VISIBLE);
            manageVisible = visibleField != null && !rowWrapper.nonChildDefaultMap.containsKey(visibleField);
            generator = dialect.getGenerator();
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

        Map<FieldMeta<?>, _Expression> rowValuesMap;
        Map<FieldMeta<?>, Object> generatedMap;
        DelayIdParamValue delayIdParam;
        _Expression expression;
        FieldMeta<?> field;
        Object value;
        GeneratorType generatorType;

        int outputValueSize = 0;

        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_VALUES);

        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
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
                } else if ((value = generatedMap.get(field)) != null) { // read the value that is generated by army
                    this.appendInsertValue(literalMode, field, value);
                } else if (field instanceof PrimaryFieldMeta && insertTable instanceof ChildTableMeta) { //child id must be managed by army
                    if (migration || (generatorType = nonChildId.generatorType()) == null) {
                        expression = rowWrapper.nonChildRowList.get(rowIndex).get(nonChildId);
                        assert expression instanceof SqlValueParam.SingleNonNamedValue;//because io.army.dialect.FieldValueGenerator have validated
                        expression.appendSql(this);
                    } else if (generatorType == GeneratorType.POST) {
                        assert delayIdParam == null && postIdMap != null;
                        delayIdParam = new DelayIdParamValue((PrimaryFieldMeta<?>) field, rowIndex, postIdMap::get);
                        this.appendParam(delayIdParam);
                    } else {
                        assert mockEnv;//must assert
                        this.appendInsertValue(literalMode, field, null);
                    }
                } else if ((expression = rowValuesMap.get(field)) != null
                        || (expression = defaultValueMap.get(field)) != null) {
                    expression.appendSql(this);
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
    public BiFunction<Integer, Object, Object> function() {
        final Map<Integer, Object> postIdMap = this.rowWrapper.postIdMap;
        assert postIdMap != null && this.isValuesClauseEnd() && this.insertTable instanceof SingleTableMeta;
        return postIdMap::putIfAbsent;
    }


    @Override
    public SimpleStmt build() {
        final SimpleStmt stmt;
        if (this.returnId == null) {
            stmt = Stmts.minSimple(this);
        } else {
            stmt = Stmts.valuePost(this);
        }
        return stmt;
    }


    @Override
    Object currentRowNamedValue(final String name) {
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


    private static final class ValuesRowWrapper extends _DialectUtils.ExpRowWrapper {

        private final List<Map<FieldMeta<?>, _Expression>> nonChildRowList;

        private final Map<FieldMeta<?>, _Expression> nonChildDefaultMap;

        private final Map<FieldMeta<?>, _Expression> childDefaultMap;

        private final Map<Integer, Object> postIdMap;

        private Map<FieldMeta<?>, Object> generatedMap;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        private ValuesRowWrapper(ValuesInsertContext context, _Insert._ValuesInsert domainStmt) {
            super(domainStmt.table(), context.parser.mappingEnv());
            this.nonChildRowList = context.rowList;
            if (domainStmt instanceof _Insert._ChildValuesInsert) {
                final _Insert._ValuesInsert parentStmt = ((_Insert._ChildValuesInsert) domainStmt).parentStmt();
                this.nonChildDefaultMap = parentStmt.defaultValueMap();
                this.childDefaultMap = domainStmt.defaultValueMap();
            } else {
                this.nonChildDefaultMap = domainStmt.defaultValueMap();
                this.childDefaultMap = Collections.emptyMap();
            }

            if (context.returnId == null) {
                postIdMap = null;
            } else {
                postIdMap = new HashMap<>((int) (this.nonChildRowList.size() / 0.75F));
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

        private final Function<Integer, Object> function;

        private DelayIdParamValue(PrimaryFieldMeta<?> field, int rowIndex, Function<Integer, Object> function) {
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
