package io.army.schema.extract;

public interface IndexColumnInfo  {

    TableInfo table();

    String name();

    IndexInfo index();

    boolean asc();

}
