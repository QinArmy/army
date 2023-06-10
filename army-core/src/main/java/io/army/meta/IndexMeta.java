package io.army.meta;



import java.util.List;

public interface IndexMeta<T> extends Meta {

    TableMeta<T> tableMeta();

    /**
     * @return index name(lower case)
     */
    String name();

    List<IndexFieldMeta<T>> fieldList();

    boolean isPrimaryKey();

    boolean isUnique();

    String type();

}
