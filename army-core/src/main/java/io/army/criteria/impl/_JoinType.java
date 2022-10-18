package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.dialect._Constant;
import io.army.util._Exceptions;
import io.army.util._StringUtils;


public enum _JoinType implements SQLWords {

    NONE(""),
    LEFT_JOIN(" LEFT JOIN"),
    JOIN(" JOIN"),
    RIGHT_JOIN(" RIGHT JOIN"),
    FULL_JOIN(" FULL JOIN"),
    CROSS_JOIN(" CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN(" STRAIGHT_JOIN");

    private final String spaceWords;

    _JoinType(String spaceWords) {
        this.spaceWords = spaceWords;
    }


    @Override
    public final String render() {
        return this.spaceWords;
    }


    @Override
    public final String toString() {
        return _StringUtils.builder()
                .append(_JoinType.class.getName())
                .append(_Constant.POINT)
                .append(this.name())
                .toString();
    }

    final void assertMySQLJoinType() {
        switch (this) {
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }

    final void assertStandardJoinType() {
        switch (this) {
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }

    final void assertNoneCrossType() {
        switch (this) {
            case NONE:
            case CROSS_JOIN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);
        }
    }


}
