package io.army.criteria.impl.inner;

import io.army.criteria.impl._Pair;
import io.army.meta.TableMeta;

import java.util.List;

public interface _MultiDelete extends _Delete, _MultiDml {

    List<_Pair<String, TableMeta<?>>> deleteTableList();


}
