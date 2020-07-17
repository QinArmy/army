package io.army.criteria.impl.inner;

import io.army.criteria.Insert;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * @see io.army.criteria.impl.SQLS#multiInsert(TableMeta)
 */
@DeveloperForbid
public interface InnerStandardInsert extends InnerMultiInsert {

    /**
     * @see Insert.InsertValuesAble
     */
    List<IDomain> valueList();
}
