package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.SetLeftItem;
import io.army.criteria.SetRightItem;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class representing standard single update context.
 * </p>
 */
final class StandardUpdateContext extends _SingleDmlContext implements _DomainUpdateContext {

    static StandardUpdateContext create(_SingleUpdate update, ArmyDialect dialect, final Visible visible) {
        final StandardUpdateContext context;
        if (update.table() instanceof ChildTableMeta) {
            context = new StandardUpdateContext(dialect, update, visible);
        } else {
            context = new StandardUpdateContext(update, dialect, visible);
        }
        return context;
    }

    final List<? extends SetLeftItem> fieldList;

    final List<? extends SetRightItem> valueExpList;

    private final ChildSetBlock childSetClause;

    private StandardUpdateContext(_SingleUpdate update, ArmyDialect dialect, Visible visible) {
        super(update, dialect, visible);

        final SingleTableMeta<?> table = (SingleTableMeta<?>) update.table();
        final List<? extends SetLeftItem> fieldList = update.leftItemList();
        for (SetLeftItem part : fieldList) {
            if (!(part instanceof FieldMeta)) {
                continue;
            }
            FieldMeta<?> field = (FieldMeta<?>) part;
            if (field.tableMeta() != table) {
                throw _Exceptions.unknownColumn(update.tableAlias(), field);
            }
        }
        this.fieldList = fieldList;
        this.valueExpList = update.rightItemList();

        this.childSetClause = null;
    }

    private StandardUpdateContext(ArmyDialect dialect, _SingleUpdate update, Visible visible) {
        super(update, dialect, visible);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) update.table();
        final SingleTableMeta<?> parentTable = childTable.parentMeta();
        final List<? extends SetLeftItem> fieldList = update.leftItemList();
        final List<? extends SetRightItem> valueExpList = update.rightItemList();
        final int fieldCount = fieldList.size();

        final List<SetLeftItem> parenFields = new ArrayList<>(), fields = new ArrayList<>();
        final List<SetRightItem> parentValues = new ArrayList<>(), values = new ArrayList<>();

        FieldMeta<?> field;
        TableMeta<?> belongOf;
        SetLeftItem part;
        for (int i = 0; i < fieldCount; i++) {
            part = fieldList.get(i);
            if (!(part instanceof FieldMeta)) {
                throw new CriteriaException("Standard update don't support Row.");
            }
            field = (FieldMeta<?>) part;
            belongOf = field.tableMeta();
            if (belongOf == parentTable) {
                parenFields.add(field);
                parentValues.add(valueExpList.get(i));
            } else if (belongOf == childTable) {
                fields.add(field);
                values.add(valueExpList.get(i));
            } else {
                throw _Exceptions.unknownColumn(update.tableAlias(), field);
            }
        }
        if (parenFields.size() == 0) {
            this.fieldList = Collections.emptyList();
            this.valueExpList = Collections.emptyList();
        } else if (fields.size() == 0) {
            this.fieldList = fieldList;
            this.valueExpList = valueExpList;
        } else {
            this.fieldList = _CollectionUtils.unmodifiableList(parenFields);
            this.valueExpList = _CollectionUtils.unmodifiableList(parentValues);
        }
        this.childSetClause = new ChildSetBlock(childTable, update.tableAlias(), fields, values, this);
    }

    /*################################## blow _SqlContext method ##################################*/


    /*################################## blow _SingleUpdateContext method ##################################*/

    @Nullable
    @Override
    public ChildSetBlock childBlock() {
        return this.childSetClause;
    }

    /*################################## blow _SetClause method ##################################*/


    @Override
    public String safeTableAlias(final TableMeta<?> table, final String alias) {
        final ChildSetBlock childBlock = this.childSetClause;
        final String safeTableAlias;
        if (childBlock == null) {
            if (table != this.table || !this.tableAlias.equals(alias)) {
                throw _Exceptions.unknownTable(table, alias);
            }
            safeTableAlias = this.safeTableAlias;
        } else if (table == childBlock.table && childBlock.tableAlias.equals(alias)) {
            safeTableAlias = childBlock.safeTableAlias;
        } else {
            throw _Exceptions.unknownTable(table, alias);
        }
        return safeTableAlias;
    }

    @Override
    public boolean hasSelfJoint() {
        //standard single update always false
        return false;
    }

    @Override
    public List<? extends SetLeftItem> leftItemList() {
        return this.fieldList;
    }

    @Override
    public List<? extends SetRightItem> valueParts() {
        return this.valueExpList;
    }

    @Override
    public String toString() {
        return this.childSetClause == null ? "standard update simple context" : "standard update parent context";
    }


    private static final class ChildSetBlock extends ChildBlock implements _SetBlock {

        final List<SetLeftItem> fieldList;

        final List<SetRightItem> valueExpList;

        private final StandardUpdateContext parentContext;

        private ChildSetBlock(ChildTableMeta<?> table, final String tableAlias
                , List<SetLeftItem> fieldList, List<SetRightItem> valueExpList
                , StandardUpdateContext parentContext) {
            super(table, tableAlias, parentContext);
            this.fieldList = _CollectionUtils.unmodifiableList(fieldList);
            this.valueExpList = _CollectionUtils.unmodifiableList(valueExpList);
            this.parentContext = parentContext;
        }

        @Override
        public boolean hasSelfJoint() {
            return this.parentContext.hasSelfJoint();
        }


        @Override
        public List<SetLeftItem> leftItemList() {
            return this.fieldList;
        }

        @Override
        public List<SetRightItem> valueParts() {
            return this.valueExpList;
        }

        @Override
        public String toString() {
            return "Standard update child context";
        }

    } // ChildSetBlock


}
