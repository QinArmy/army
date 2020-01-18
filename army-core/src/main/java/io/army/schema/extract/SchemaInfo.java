package io.army.schema.extract;

import java.util.List;

public interface SchemaInfo {

    String catalog();

    String name();

    List<TableInfo> tables();

}
