package io.army.criteria.mysql;

import io.army.criteria.dialect.Window;
import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

public interface MySQLWindows extends Window.Builder {


    Window._SimplePartitionBySpec window(String windowName, SQLs.WordAs as);

    Window._SimplePartitionBySpec window(String windowName, SQLs.WordAs as, @Nullable String existingWindowName);


}
