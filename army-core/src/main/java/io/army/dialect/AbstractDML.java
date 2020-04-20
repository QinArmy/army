package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.BeanWrapper;
import io.army.boot.FieldValuesGenerator;
import io.army.criteria.*;
import io.army.criteria.impl.CriteriaCounselor;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.*;
import io.army.domain.IDomain;
import io.army.generator.PostMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.util.Assert;
import io.army.util.ClassUtils;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class AbstractDML implements DML {

    private final Dialect dialect;
    private Collection<FieldMeta<?, ?>> childFields;

    public AbstractDML(Dialect dialect) {
        this.dialect = dialect;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public final String quoteIfNeed(String identifier) {
        return dialect.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return dialect.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return dialect.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return dialect.sessionFactory();
    }

    /*################################## blow DML batchInsert method ##################################*/

    @Override
    public final List<SQLWrapper> insert(IDomain domain) {
        Assert.notNull(domain, "domain required");

        final SessionFactory sessionFactory = this.dialect.sessionFactory();

        TableMeta<?> tableMeta = sessionFactory.tableMetaMap().get(domain.getClass());
        //  create necessary value for domain
        BeanWrapper beanWrapper = FieldValuesGenerator.build(sessionFactory)
                .createValues(tableMeta, domain);

        return Collections.unmodifiableList(
                insertDomain(tableMeta, beanWrapper, tableMeta.fieldCollection(), null)
        );

    }


    @Override
    public final List<SQLWrapper> insert(Insert insert) {
        List<SQLWrapper> list;
        if (insert instanceof InnerStandardInsert) {
            list = standardInsert((InnerStandardInsert) insert);
        } else if (insert instanceof InnerStandardSubQueryInsert) {
            list = standardSubQueryInsert((InnerStandardSubQueryInsert) insert);
        } else if (insert instanceof InnerSpecialInsert) {
            list = specialInsert((InnerSpecialInsert) insert);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }

        return Collections.unmodifiableList(list);
    }

    @Override
    public final List<BatchSQLWrapper> batchInsert(Insert insert) {
        List<BatchSQLWrapper> list;
        if (insert instanceof InnerStandardBatchInsert) {
            list = standardBatchInsert((InnerStandardBatchInsert) insert);
        } else if (insert instanceof InnerSpecialInsert) {
            list = specialBatchInsert((InnerSpecialInsert) insert);
        } else {
            throw new IllegalArgumentException(String.format("Insert[%s] type unknown.", insert.getClass().getName()));
        }
        return Collections.unmodifiableList(list);
    }

    /*################################## blow update method ##################################*/

    @Override
    public final List<SQLWrapper> update(Update update, Visible visible) {
        Assert.isInstanceOf(InnerUpdate.class, update, "");
        InnerUpdate innerAble = (InnerUpdate) update;

        List<SQLWrapper> wrapperList;
        if (update instanceof InnerObjectUpdate) {
            // create singleUpdate dml for child mapping mode
            wrapperList = createObjectUpdate((InnerObjectUpdate) innerAble, visible);
        } else {
            wrapperList = createUpdateForSimple(innerAble, visible);
        }
        return wrapperList;
    }

    @Override
    public List<SQLWrapper> delete(Delete.DeleteAble singleDeleteAble, Visible visible) {
       /* InnerDelete deleteAble = (InnerDelete) singleDeleteAble;
        TableMeta<?> tableMeta = deleteAble.tableMeta();
        List<SQLWrapper> list;
        if (tableMeta.parentMeta() == null) {
            list = createDeleteForSimple(deleteAble, visible);
        } else {
            list = createDeleteForChild(deleteAble, visible);
        }*/
        return Collections.emptyList();
    }

    /*################################## blow package batchInsert template method ##################################*/

    protected InsertContext createInsertContext(@Nullable InnerInsert insert) {
        InsertContext context;
        if (insert == null) {
            context = new StandardInsertContext(this, this.dialect);
        } else {
            context = new StandardInsertContext(this, this.dialect, insert);
        }
        return context;
    }

    protected abstract List<SQLWrapper> specialInsert(InnerSpecialInsert insert);

    protected abstract List<BatchSQLWrapper> specialBatchInsert(InnerSpecialInsert insert);

    /*################################## blow protected method ##################################*/

    /*################################## blow private batchInsert method ##################################*/

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> standardInsert(InnerStandardInsert insert) {
        CriteriaCounselor.assertInsert(insert);

        List<IDomain> domainList = insert.valueList();
        TableMeta<?> tableMeta = insert.tableMeta();
        // 1. get target fields.
        Collection<FieldMeta<?, ?>> fieldMetas = insert.fieldList();
        if (CollectionUtils.isEmpty(fieldMetas)) {
            fieldMetas = Collections.unmodifiableCollection(tableMeta.fieldCollection());
        }

        List<SQLWrapper> sqlWrapperList;
        if (tableMeta instanceof ChildTableMeta) {
            sqlWrapperList = new ArrayList<>(domainList.size() * 2);
        } else {
            sqlWrapperList = new ArrayList<>(domainList.size());
        }

        FieldValuesGenerator valuesGenerator = FieldValuesGenerator.build(this.dialect.sessionFactory());
        BeanWrapper beanWrapper;

        for (IDomain domain : domainList) {
            // 2. create required values.
            beanWrapper = valuesGenerator.createValues(tableMeta, domain);
            sqlWrapperList.addAll(
                    insertDomain(tableMeta, beanWrapper, fieldMetas, insert)
            );
        }
        return sqlWrapperList;
    }

    private List<SQLWrapper> standardSubQueryInsert(InnerStandardSubQueryInsert insert) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<FieldMeta<?, ?>> fieldMetaList = insert.fieldList();
        int subQuerySelectionCount = DMLUtils.selectionCount(insert.subQuery());

        if (subQuerySelectionCount != fieldMetaList.size()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "selection size[%s] of SubQuery and targetFieldList size[%s] not match."
                    , subQuerySelectionCount, fieldMetaList.size());
        }

        InsertContext context = createInsertContext(insert);
        StringBuilder builder = context.fieldStringBuilder().append("INSERT INTO ");
        context.appendTable(tableMeta);
        builder.append(" ( ");

        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
            index++;
        }
        builder.append(" )");
        insert.subQuery().appendSQL(context);
        return Collections.singletonList(DMLUtils.createSQLWrapper(context));
    }


    private List<SQLWrapper> insertDomain(TableMeta<?> tableMeta, BeanWrapper entityWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas
            , @Nullable InnerInsert innerInsert) {

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        createInsertForSimple(tableMeta, entityWrapper, fieldMetas, innerInsert)
                );
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild((ChildTableMeta<?>) tableMeta
                        , entityWrapper, fieldMetas, innerInsert);
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        createInsertForParent(tableMeta, entityWrapper, fieldMetas, innerInsert)
                );
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return sqlWrapperList;
    }


    private List<SQLWrapper> createInsertForChild(ChildTableMeta<?> childMeta
            , BeanWrapper beanWrapper, @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas
            , @Nullable InnerInsert innerInsert) {

        TableMeta<?> parentMeta = childMeta.parentMeta();
        Collection<FieldMeta<?, ?>> childFields, parentFields;
        if (fieldMetas == null) {
            // unmodifiableCollection for avoid generic error in below part
            parentFields = Collections.unmodifiableCollection(parentMeta.fieldCollection());
            childFields = Collections.unmodifiableCollection(childMeta.fieldCollection());
        } else {

            Set<FieldMeta<?, ?>> parentFieldSet = new HashSet<>(parentMeta.fieldCollection());
            Set<FieldMeta<?, ?>> childFieldSet = new HashSet<>(childMeta.fieldCollection());

            childFields = new ArrayList<>();
            parentFields = new ArrayList<>();

            for (FieldMeta<?, ?> fieldMeta : fieldMetas) {
                if (childFieldSet.contains(fieldMeta)) {
                    childFields.add(fieldMeta);
                } else if (parentFieldSet.contains(fieldMeta)) {
                    parentFields.add(fieldMeta);
                } else {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "FieldMeta[%s] and ChildTableMeta[%s] not match."
                            , fieldMeta, childMeta);
                }
            }

        }

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        sqlWrapperList.add(
                createInsertForParent(parentMeta, beanWrapper, parentFields, innerInsert)
        );

        InsertContext context = createInsertContext(innerInsert);
        DMLUtils.createInsertForSimple(childMeta, childFields, beanWrapper, context);
        sqlWrapperList.add(DMLUtils.createSQLWrapper(context));

        return sqlWrapperList;
    }


    /**
     * @return a modifiable list
     */
    private SQLWrapper createInsertForParent(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, @Nullable InnerInsert innerInsert) {
        return createInsertForSimple(tableMeta, beanWrapper, fieldMetas, innerInsert);
    }

    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, BeanWrapper beanWrapper
            , @Nullable Collection<? extends FieldMeta<?, ?>> fieldMetas, @Nullable InnerInsert innerInsert) {
        Collection<? extends FieldMeta<?, ?>> targetFields = fieldMetas;
        if (targetFields == null) {
            // unmodifiableCollection for avoid generic error
            targetFields = tableMeta.fieldCollection();
        }
        InsertContext context = createInsertContext(innerInsert);
        DMLUtils.createInsertForSimple(tableMeta, targetFields, beanWrapper, context);

        SQLWrapper sqlWrapper;
        GeneratorMeta generatorMeta = tableMeta.primaryKey().generator();

        if (generatorMeta != null
                && ClassUtils.isAssignable(PostMultiGenerator.class, generatorMeta.type())) {
            sqlWrapper = DMLUtils.createSQLWrapper(context, beanWrapper);
        } else {
            sqlWrapper = DMLUtils.createSQLWrapper(context);
        }
        return sqlWrapper;
    }

    private List<BatchSQLWrapper> standardBatchInsert(InnerStandardBatchInsert insert) {
        CriteriaCounselor.assertInsert(insert);

        TableMeta<?> tableMeta = insert.tableMeta();
        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForSimple(insert)
                );
                break;
            case PARENT:
                sqlWrapperList = Collections.singletonList(
                        standardBatchInsertForParent(insert)
                );
                break;
            case CHILD:
                sqlWrapperList = standardBatchInsertForChild(insert);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return DMLUtils.createBatchInsertWrapper(
                insert
                , sqlWrapperList
                , FieldValuesGenerator.build(this.dialect.sessionFactory())
        );
    }


    private SQLWrapper standardBatchInsertForSimple(InnerStandardBatchInsert insert) {
        InsertContext context = createInsertContext(insert);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        return DMLUtils.createSQLWrapper(context);
    }

    private SQLWrapper standardBatchInsertForParent(InnerStandardBatchInsert insert) {
        return standardBatchInsertForSimple(insert);
    }

    private List<SQLWrapper> standardBatchInsertForChild(InnerStandardBatchInsert insert) {
        ChildTableMeta<?> childMeta = (ChildTableMeta<?>) insert.tableMeta();
        TableMeta<?> parentMeta = childMeta.parentMeta();

        List<SQLWrapper> sqlWrapperList = new ArrayList<>(2);
        // 1. parent sql wrapper
        sqlWrapperList.add(
                standardBatchInsertForParent(insert)
        );
        //2. child sql wrapper
        InsertContext context = createInsertContext(insert);
        DMLUtils.createBatchInsertForSimple(insert.tableMeta(), context);
        sqlWrapperList.add(
                DMLUtils.createSQLWrapper(context)
        );
        return Collections.unmodifiableList(sqlWrapperList);
    }


    private void assertStandardBatchInsert(InnerStandardInsert insert) {
        if (!CollectionUtils.isEmpty(insert.fieldList()) || insert.defaultExpIfNull()) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "fieldList required for batch batchInsert.");
        }
    }


    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createDeleteForSimple(InnerDelete deleteAble, Visible visible) {

       /* final SQLContext context = new DefaultSQLContext(this, SQLStatement.DELETE);
        TableMeta<?> tableMeta = deleteAble.tableMeta();

        final String tableName = this.sql.quoteIfNeed(tableMeta.tableName());
        // 1. singleDelete clause
        appendDeleteClause(context, tableName, deleteAble);
        // 2. where clause
        appendDeleteWhereClause(context, deleteAble, visible);
        return Collections.singletonList(
                SQLWrapper.build(context.stringBuilder().toString(), context.paramWrapper())
        );*/
        return Collections.emptyList();
    }

    private void appendDeleteClause(SQLContext context, String tableName, InnerDelete deleteAble) {
        context.stringBuilder()
                .append("DELETE FROM ")
                .append(tableName);

    }

    private void appendDeleteWhereClause(SQLContext context, InnerDelete deleteAble, Visible visible) {

        /*List<IPredicate> predicateList = deleteAble.predicateList();
        Assert.notEmpty(predicateList, "no where clause forbidden by army");

        StringBuilder builder = context.stringBuilder()
                .append(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
        }
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            FieldMeta<?, ?> visibleField = deleteAble.tableMeta().getField(TableMeta.VISIBLE);
            String textValue = visibleField.mappingType().nonNullTextValue(visibleValue);
            builder.append(" AND ")
                    .append(this.quoteIfNeed(visibleField.fieldName()))
                    .append(" = ")
                    .append(DialectUtils.quoteIfNeed(visibleField.mappingType(), textValue))
            ;
        }*/

    }

    /**
     * @return a unmodifiable list
     */
    private List<SQLWrapper> createObjectUpdate(InnerObjectUpdate updateAble, Visible visible) {
        TableMeta<?> childMeta = null, parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        ObjectUpdateContextImpl context = new ObjectUpdateContextImpl(this, this.dialect, childMeta, null);
        // 1. singleUpdate clause
        appendObjectUpdateClause(context);
        // 2. set clause
        appendSetClause(context, updateAble.targetFieldList(), updateAble.valueExpList());
        // 3. where clause
        // appendWhereClause(context, updateAble.predicateList());
        //4. append child visible
        appendVisiblePredicate(parentMeta, context, context.safeParentAlias(), visible);

        return Collections.singletonList(
                SQLWrapper.build(context.builder.toString(), context.paramWrapperList)
        );
    }

    private void appendObjectUpdateClause(ObjectUpdateContextImpl context) {
        TableMeta<?> childMeta = context.tableMeta(), parentMeta = childMeta.parentMeta();
        Assert.notNull(parentMeta, () -> String.format("Table[%s] not child mode", childMeta.tableName()));

        StringBuilder builder = context.stringBuilder()
                .append("UPDATE ")
                .append(this.dialect.quoteIfNeed(childMeta.tableName()));

        if (StringUtils.hasText(context.safeAlias())) {
            builder.append(" AS ")
                    .append(context.safeAlias());
        }
        builder.append(" JOIN ")
                .append(this.dialect.quoteIfNeed(parentMeta.tableName()))
                .append(" AS ")
                .append(context.safeParentAlias())
                .append(" ON ")
                .append(context.safeAlias())
                .append(".")
                .append(TableMeta.ID)
                .append(" = ")
                .append(context.safeParentAlias())
                .append(".")
                .append(TableMeta.ID)
        ;
    }

    private void appendSetClause(UpdateSQLContext context, List<FieldMeta<?, ?>> fieldMetaList
            , List<Expression<?>> valueExpList) {

        Assert.notEmpty(fieldMetaList, "set clause must not empty");
        Assert.isTrue(fieldMetaList.size() == valueExpList.size(), "field list ifAnd value exp list size not match");

        final int size = fieldMetaList.size();
        FieldMeta<?, ?> fieldMeta;
        Expression<?> valueExp;

        StringBuilder builder = context.stringBuilder()
                .append(" SET");
        for (int i = 0; i < size; i++) {
            fieldMeta = fieldMetaList.get(i);
            valueExp = valueExpList.get(i);

            context.assertField(fieldMeta);

            // fieldMeta self-describe
            fieldMeta.appendSQL(context);
            builder.append(" =");
            // expression self-describe
            valueExp.appendSQL(context);
            if (i < size - 1) {
                builder.append(",");
            }
        }
        // append version ifAnd updateTime
        appendFieldsManagedByArmy(context);

    }


    private void appendWhereClause(SQLContext context, List<IPredicate> predicateList) {
        Assert.notEmpty(predicateList, "where clause must be not empty");

        StringBuilder builder = context.stringBuilder()
                .append(" WHERE");

        for (Iterator<IPredicate> iterator = predicateList.iterator(); iterator.hasNext(); ) {
            // predicate self-describe
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(" AND");
            }
        }
    }


    private List<SQLWrapper> createUpdateForSimple(InnerUpdate innerAble, Visible visible) {

        // build dml context
        final UpdateSQLContextImpl context = new UpdateSQLContextImpl(this, this.dialect, null, null);

        //1. singleUpdate clause
        appendUpdateClause(context);
        //2. set clause
        appendSetClause(context, innerAble.targetFieldList(), innerAble.valueExpList());
        //3. where clause
        // appendWhereClause(context, innerAble.predicateList());
        //4. append visible
        appendVisiblePredicate(context.updateTable, context, context.safeAlias(), visible);

        return Collections.singletonList(
                SQLWrapper.build(context.builder.toString(), context.paramWrapperList)
        );
    }

    private void appendUpdateClause(UpdateSQLContext context) {
        context.stringBuilder().append("UPDATE ")
                .append(this.dialect.quoteIfNeed(context.tableMeta().tableName()));

        if (StringUtils.hasText(context.safeAlias())) {
            context.stringBuilder()
                    .append(" AS ")
                    .append(context.safeAlias());
        }
    }

    private void appendFieldsManagedByArmy(UpdateSQLContext context) {
        //1. version field
        FieldMeta<?, ?> fieldMeta = context.versionField();
        StringBuilder builder = context.stringBuilder();

        builder.append(",");
        fieldMeta.appendSQL(context);

        builder.append(" = ");
        fieldMeta.add(SQLS.constant(1)).appendSQL(context);

        //2. updateTime field
        fieldMeta = context.updateTimeField();
        builder.append(",");
        // updateTime field self-describe
        fieldMeta.appendSQL(context);
        builder.append(" = ");

        if (fieldMeta.javaType() == LocalDateTime.class) {
            SQLS.param(LocalDateTime.now()).appendSQL(context);
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            SQLS.param(ZonedDateTime.now(this.zoneId())).appendSQL(context);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }


    private void appendVisiblePredicate(TableMeta<?> tableMetaWithVisible, SQLContext context
            , final String safeTableAlias, Visible visible) {
        final FieldMeta<?, ?> visibleField = tableMetaWithVisible.getField(TableMeta.VISIBLE);

        Assert.state(visibleField.javaType() == Boolean.class, "visible prop class type only is Boolean");
        // append visible field
        Boolean visibleValue = visible.getValue();
        if (visibleValue != null) {
            StringBuilder builder = context.stringBuilder().append(" AND ");
            if (StringUtils.hasText(safeTableAlias)) {
                builder.append(safeTableAlias)
                        .append(".");
            }
            builder.append(this.dialect.quoteIfNeed(visibleField.fieldName()))
                    .append(" = ? ");
            context.appendParam(ParamWrapper.build(visibleField.mappingType(), visibleValue));
        }

    }

    private void appendOrderByClause(SQLContext context, List<Expression<?>> orderExpList, List<Boolean> ascExpList) {
        if (CollectionUtils.isEmpty(orderExpList)) {
            return;
        }
        Assert.isTrue(orderExpList.size() == ascExpList.size(), "orderExpList size havingAnd ascExpList size not match.");

        StringBuilder builder = context.stringBuilder()
                .append(" ORDER BY");

        Expression<?> orderExp;
        Boolean ascExp;
        final int size = orderExpList.size();
        for (int i = 0; i < size; i++) {
            orderExp = orderExpList.get(i);
            orderExp.appendSQL(context);
            ascExp = ascExpList.get(i);

            if (Boolean.TRUE.equals(ascExp)) {
                builder.append(" ASC");
            } else if (Boolean.FALSE.equals(ascExp)) {
                builder.append(" DESC");
            }
            if (i < size - 1) {
                builder.append(",");
            }
        }
    }

    private void appendLimitClause(SQLContext context, int rowCount) {
        if (rowCount > -1) {
            context.stringBuilder().append(" LIMIT ?");
            context.appendParam(
                    ParamWrapper.build(MappingFactory.getDefaultMapping(Integer.class), rowCount)
            );
        }
    }


    private void assertTargetField(FieldMeta<?, ?> fieldMeta, TableMeta<?> tableMeta) {
        Assert.isTrue(fieldMeta.tableMeta() == tableMeta, () -> String.format(
                "field[%s] don'field belong to tableMeta[%s]", fieldMeta, tableMeta));

        if (!fieldMeta.updatable()) {
            throw new NonUpdateAbleException(ErrorCode.NON_UPDATABLE
                    , String.format("domain[%s] field[%s] is non-updatable"
                    , fieldMeta.tableMeta().javaType().getName(), fieldMeta.propertyName()));
        }
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())
                || TableMeta.UPDATE_TIME.equals(fieldMeta.propertyName())) {
            throw new IllegalArgumentException("version or updateTime is managed by army.");
        }
    }

    private void assertFieldAndValueExpressionMatch(FieldMeta<?, ?> fieldMeta, Expression<?> valueExp) {
        if (!fieldMeta.nullable()
                && valueExp instanceof ParamExpression
                && ((ParamExpression<?>) valueExp).value() == null) {
            throw new IllegalArgumentException(String.format("domain[%s] mapping prop[%s] non-null"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()));
        }
    }


}
