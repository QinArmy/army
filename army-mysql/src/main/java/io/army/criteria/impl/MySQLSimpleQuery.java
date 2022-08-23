package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLModifier;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQL80SimpleQuery}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQLSimpleQuery<C, Q extends Query, WE, SR, FT, FS, FP, FJ, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        extends WithCteSimpleQuery<C, Q, SubQuery, WE, MySQLModifier, SR, FT, FS, FP, FJ, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        implements _MySQLQuery, MySQLQuery._IntoSpec<C, Q> {


    private List<String> intoVarList;

    MySQLSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }



    @Override
    public final _QuerySpec<Q> into(String varName) {
        this.intoVarList = Collections.singletonList(varName);
        return this;
    }

    @Override
    public final _QuerySpec<Q> into(String varName1, String varName2) {
        this.intoVarList = ArrayUtils.asUnmodifiableList(varName1, varName2);
        return this;
    }

    @Override
    public final _QuerySpec<Q> into(String varName1, String varName2, String varName3) {
        this.intoVarList = ArrayUtils.asUnmodifiableList(varName1, varName2, varName3);
        return this;
    }

    @Override
    public final _QuerySpec<Q> into(List<String> varNameList) {
        if (varNameList.size() == 0) {
            throw MySQLUtils.intoVarListNotEmpty();
        }
        this.intoVarList = _CollectionUtils.asUnmodifiableList(varNameList);
        return this;
    }

    @Override
    public final _QuerySpec<Q> into(Supplier<List<String>> supplier) {
        return this.into(supplier.get());
    }

    @Override
    public final _QuerySpec<Q> into(Function<C, List<String>> function) {
        return this.into(function.apply(this.criteria));
    }

    @Override
    public final _QuerySpec<Q> into(Consumer<List<String>> consumer) {
        final List<String> varNameList = new ArrayList<>();
        consumer.accept(varNameList);
        switch (varNameList.size()) {
            case 0:
                throw MySQLUtils.intoVarListNotEmpty();
            case 1:
                this.intoVarList = Collections.singletonList(varNameList.get(0));
                break;
            default:
                this.intoVarList = Collections.unmodifiableList(varNameList);
        }
        return this;
    }

    @Override
    public final List<String> intoVarList() {
        this.prepared();
        return this.intoVarList;
    }


    @Override
    Q onAsQuery(boolean fromAsQueryMethod) {
        if (this.intoVarList == null) {
            this.intoVarList = Collections.emptyList();
        }
        //here, couldn't invoke this.finallyAsQuery method.
        return (Q) this;
    }

    @Override
    void onClear() {
        this.intoVarList = null;
    }

    @Override
    final List<MySQLModifier> asModifierList(@Nullable List<MySQLModifier> modifiers) {
        return MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::selectModifier);
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return MySQLUtils.asHintList(this.context, hints, MySQLHints::castHint);
    }

    /*################################## blow private method ##################################*/



    enum MySQLLockMode implements SQLWords {

        FOR_UPDATE(_Constant.FOR_UPDATE),
        LOCK_IN_SHARE_MODE(_Constant.LOCK_IN_SHARE_MODE),
        FOR_SHARE(_Constant.FOR_SHARE);

        final String words;

        MySQLLockMode(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


        @Override
        public final String toString() {
            return String.format("%s.%s", MySQLLockMode.class.getSimpleName(), this.name());
        }

    }//MySQLLock

    enum MySQLLockOption implements SQLWords {

        NOWAIT(" NOWAIT"),
        SKIP_LOCKED(" SKIP LOCKED");

        final String words;

        MySQLLockOption(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", MySQLLockOption.class.getSimpleName(), this.name());
        }

    }//MySQLLockOption


}
