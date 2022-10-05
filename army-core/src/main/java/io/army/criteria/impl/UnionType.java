package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.util._Exceptions;
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


    static UnionType from(final CriteriaContext context, final UnionType unionType
            , final @Nullable Query.UnionModifier modifier) {
        final UnionType actualType;
        switch (unionType) {
            case UNION: {
                if (modifier == null) {
                    actualType = unionType;
                } else if (modifier == SQLs.DISTINCT) {
                    actualType = UnionType.UNION_DISTINCT;
                } else if (modifier == SQLs.ALL) {
                    actualType = UnionType.UNION_ALL;
                } else {
                    throw errorUnionModifier(context, modifier);
                }
            }
            break;
            case EXCEPT: {
                if (modifier == null) {
                    actualType = unionType;
                } else if (modifier == SQLs.DISTINCT) {
                    actualType = UnionType.EXCEPT_DISTINCT;
                } else if (modifier == SQLs.ALL) {
                    actualType = UnionType.EXCEPT_ALL;
                } else {
                    throw errorUnionModifier(context, modifier);
                }
            }
            break;
            case MINUS: {
                if (modifier == null) {
                    actualType = unionType;
                } else if (modifier == SQLs.DISTINCT) {
                    actualType = UnionType.MINUS_DISTINCT;
                } else if (modifier == SQLs.ALL) {
                    actualType = UnionType.MINUS_ALL;
                } else {
                    throw errorUnionModifier(context, modifier);
                }
            }
            break;
            case INTERSECT: {
                if (modifier == null) {
                    actualType = unionType;
                } else if (modifier == SQLs.DISTINCT) {
                    actualType = UnionType.INTERSECT_DISTINCT;
                } else if (modifier == SQLs.ALL) {
                    actualType = UnionType.INTERSECT_ALL;
                } else {
                    throw errorUnionModifier(context, modifier);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(unionType);
        }
        return actualType;
    }


    private static CriteriaException errorUnionModifier(CriteriaContext context
            , @Nullable Query.UnionModifier modifier) {
        String m = String.format("%s error %s", Query.UnionModifier.class.getName(), modifier);
        return ContextStack.criteriaError(context, m);
    }


}
