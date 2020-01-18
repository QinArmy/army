package io.army.schema.extract;

import java.util.List;

public interface IndexInfo {

    TableInfo table();

    String name();

    List<IndexColumnInfo> columns();

    boolean unique();

}
