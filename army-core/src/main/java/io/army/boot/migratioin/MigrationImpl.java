package io.army.boot.migratioin;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MigrationImpl implements Migration {

    private final TableMeta<?> table;

    private final boolean newTable;

    private List<FieldMeta<?, ?>> columnsToAdd;

    private List<FieldMeta<?, ?>> columnsToModify;

    private List<IndexMeta<?>> indexesToAdd;

    private List<IndexMeta<?>> indexesToModify;

    private List<String> indexesToDrop;

    private boolean finalFinish;


    MigrationImpl(TableMeta<?> table, boolean newTable) {
        this.table = table;
        this.newTable = newTable;
    }

    @Override
    public TableMeta<?> table() {
        return table;
    }

    @Override
    public boolean newTable() {
        return newTable;
    }

    @Override
    public List<FieldMeta<?, ?>> columnsToAdd() {
        return columnsToAdd;
    }

    void addColumnToAdd(FieldMeta<?, ?> columnToAdd) {
        assertNotFinal();
        if (this.columnsToAdd == null) {
            this.columnsToAdd = new ArrayList<>();
        }
        this.columnsToAdd.add(columnToAdd);
    }

    @Override
    public List<FieldMeta<?, ?>> columnsToChange() {
        return columnsToModify;
    }

    void addColumnToModify(FieldMeta<?, ?> columnToModify) {
        assertNotFinal();
        if (this.columnsToModify == null) {
            this.columnsToModify = new ArrayList<>();
        }
        this.columnsToModify.add(columnToModify);
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
        return !CollectionUtils.isEmpty(columnsToAdd)
                || !CollectionUtils.isEmpty(columnsToModify)
                || !CollectionUtils.isEmpty(indexesToAdd)
                || !CollectionUtils.isEmpty(indexesToModify)
                || !CollectionUtils.isEmpty(indexesToDrop)
                ;
    }

    void makeFinal() {
        if (finalFinish) {
            return;
        }
        if (needAlter()) {
            columnsToAdd = Collections.unmodifiableList(columnsToAdd);
            columnsToModify = Collections.unmodifiableList(columnsToModify);
            indexesToAdd = Collections.unmodifiableList(indexesToAdd);
            indexesToModify = Collections.unmodifiableList(indexesToModify);
            indexesToDrop = Collections.unmodifiableList(indexesToDrop);
        } else {
            columnsToAdd = Collections.emptyList();
            columnsToModify = Collections.emptyList();
            indexesToAdd = Collections.emptyList();
            indexesToModify = Collections.emptyList();
            indexesToDrop = Collections.emptyList();
        }

        finalFinish = true;

    }

    /*################################## blow private method ##################################*/

    private void assertNotFinal(){
        if(finalFinish){
            throw new UnsupportedOperationException();
        }
    }



}
