package io.army.util;

import io.army.NonUniqueException;
import io.army.Session;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.List;

public abstract class SyncCriteriaUtils extends CriteriaUtils {


    @Nullable
    public static <T extends IDomain> T getByUnique(Session session, Class<T> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return getByUnique(session, domainClass, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @Nullable
    public static <T extends IDomain> T getByUnique(Session session, Class<T> domainClass, List<String> propNameList
            , List<Object> valueList, Visible visible) {

        Select select = createSelectDomainByUnique(domainClass, propNameList, valueList);
        List<T> list = session.select(select, domainClass, visible);

        T domain;
        if (list.size() == 1) {
            domain = list.get(0);
        } else if (list.size() > 1) {
            throw new NonUniqueException("propNameList[%s] don't select unique domain.", propNameList);
        } else {
            domain = null;
        }
        return domain;
    }
}
