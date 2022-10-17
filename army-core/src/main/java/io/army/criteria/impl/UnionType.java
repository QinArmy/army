package io.army.criteria.impl;

import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

enum UnionType implements SQLWords, Query.UnionModifier {
    UNION(" UNION"),
    UNION_ALL(" UNION ALL"),
    UNION_DISTINCT(" UNION DISTINCT"),

    INTERSECT(" INTERSECT"),
    INTERSECT_ALL(" INTERSECT ALL"),
    INTERSECT_DISTINCT(" INTERSECT DISTINCT"),

    EXCEPT(" EXCEPT"),
    EXCEPT_ALL(" EXCEPT ALL"),
    EXCEPT_DISTINCT(" EXCEPT DISTINCT"),

    MINUS(" MINUS"),
    MINUS_ALL(" MINUS ALL"),
    MINUS_DISTINCT(" MINUS DISTINCT");

    final String spaceWords;

    UnionType(String keyWords) {
        this.spaceWords = keyWords;
    }


    @Override
    public final String render() {
        return this.spaceWords;
    }


    @Override
    public final String toString() {
        return _StringUtils.builder()
                .append(UnionType.class.getSimpleName())
                .append(_Constant.POINT)
                .append(this.name())
                .toString();
    }

    static void standardUnionType(final CriteriaContext context, final UnionType unionType) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            default:
                throw ContextStack.castCriteriaApi(context);
        }
    }



}
