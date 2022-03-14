package io.army.boot.migratioin;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class MigrationMemberImpl implements MigrationMember {

    private final String tableSuffix;

    private final TableMeta<?> table;

    private final boolean newTable;

    private boolean modifyTableComment;

    private List<FieldMeta<?>> columnsToAdd;

    private List<FieldMeta<?>> columnsToModify;

    private List<IndexMeta<?>> indexesToAdd;

    private List<IndexMeta<?>> indexesToModify;

    private List<FieldMeta<?>> commentsToModify;

    private List<String> indexesToDrop;

    private boolean finalFinish;


    MigrationMemberImpl(TableMeta<?> table, @Nullable String tableSuffix, boolean newTable) {
        this.table = table;
        this.tableSuffix = tableSuffix;
        this.newTable = newTable;
        if (newTable) {
            makeFinal();
        }
    }

    @Nullable
    @Override
    public String tableSuffix() {
        return this.tableSuffix;
    }

    @Override
    public TableMeta<?> table() {
        return table;
    }

    @Override
    public String actualTableName() {
        return this.tableSuffix == null
                ? this.table.tableName()
                : (table.tableName() + this.tableSuffix);
    }

    @Override
    public boolean newTable() {
        return newTable;
    }


    @Override
    public boolean modifyTableComment() {
        return this.modifyTableComment;
    }

    @Override
    public List<FieldMeta<?>> columnsToAdd() {
        return columnsToAdd;
    }

    void addColumnToAdd(FieldMeta<?> columnToAdd) {
        assertNotFinal();
        if (this.columnsToAdd == null) {
            this.columnsToAdd = new ArrayList<>();
        }
        this.columnsToAdd.add(columnToAdd);
    }

    @Override
    public List<FieldMeta<?>> columnsToChange() {
        return columnsToModify;
    }

    @Override
    public List<FieldMeta<?>> commentToModify() {
        return this.commentsToModify;
    }

    void modifyTableComment(boolean modify) {
        this.modifyTableComment = modify;
    }

    void addColumnToModify(FieldMeta<?> columnToModify) {
        assertNotFinal();
        if (this.columnsToModify == null) {
            this.columnsToModify = new ArrayList<>();
        }
        this.columnsToModify.add(columnToModify);
    }

    void addCommentToModify(FieldMeta<?> commentToModify) {
        assertNotFinal();
        if (this.commentsToModify == null) {
            this.commentsToModify = new ArrayList<>();
        }
        this.commentsToModify.add(commentToModify);
    }

    @Override
    public List<IndexMeta<?>> indexesToAdd() {
        return indexesToAdd;
    }

    void addIndexToAdd(IndexMeta<?> indexesToAdd) {
        assertNotFinal();
        if (this.indexesToAdd == null) {
            this.indexesToAdd = new ArrayList<>();
        }
        this.indexesToAdd.add(indexesToAdd);
    }

    @Override
    public List<IndexMeta<?>> indexesToAlter() {
        return indexesToModify;
    }

    void addIndexToModify(IndexMeta<?> indexToModify) {
        assertNotFinal();
        if (this.indexesToModify == null) {
            this.indexesToModify = new ArrayList<>();
        }
        this.indexesToModify.add(indexToModify);
    }

    @Override
    public List<String> indexesToDrop() {
        return indexesToDrop;
    }

    void addIndexToDrop(String indexName) {
        assertNotFinal();
        if (this.indexesToDrop == null) {
            this.indexesToDrop = new ArrayList<>();
        }
        this.indexesToDrop.add(indexName);
    }

    boolean needAlter() {
        return !CollectionUtils.isEmpty(this.columnsToAdd)
                || !CollectionUtils.isEmpty(this.columnsToModify)
                || !CollectionUtils.isEmpty(this.indexesToAdd)
                || !CollectionUtils.isEmpty(this.indexesToModify)
                || !CollectionUtils.isEmpty(this.indexesToDrop)
                || !CollectionUtils.isEmpty(this.commentsToModify)
                ;
    }

    void makeFinal() {
        if (finalFinish) {
            return;
        }

        if (needAlter()) {
            this.columnsToAdd = this.unmodifiableList(this.columnsToAdd);
            this.columnsToModify = this.unmodifiableList(this.columnsToModify);
            this.indexesToAdd = this.unmodifiableList(this.indexesToAdd);
            this.indexesToModify = this.unmodifiableList(this.indexesToModify);
            this.indexesToDrop = this.unmodifiableList(this.indexesToDrop);
            this.commentsToModify = this.unmodifiableList(this.commentsToModify);
        } else {
            this.columnsToAdd = Collections.emptyList();
            this.columnsToModify = Collections.emptyList();
            this.indexesToAdd = Collections.emptyList();
            this.indexesToModify = Collections.emptyList();
            this.indexesToDrop = Collections.emptyList();
        }

        this.finalFinish = true;

    }



    /*################################## blow private method ##################################*/

    private <T> List<T> unmodifiableList(@Nullable List<T> list) {
        List<T> unmodifiableList;
        if (list == null) {
            unmodifiableList = Collections.emptyList();
        } else {
            unmodifiableList = Collections.unmodifiableList(list);
        }
        return unmodifiableList;
    }

    private void assertNotFinal() {
        if (this.finalFinish) {
            throw new UnsupportedOperationException();
        }
    }


}
