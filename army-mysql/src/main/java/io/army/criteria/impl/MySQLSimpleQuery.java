package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
abstract class MySQLSimpleQuery<C, Q extends Query, WE, SR, FT, FS, FP, IR, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        extends WithCteSimpleQuery<C, Q, SubQuery, WE, MySQLWords, SR, FT, FS, FP, JT, JS, JP, WR, AR, GR, HR, OR, LR, UR, SP>
        implements _MySQLQuery, MySQLQuery._IndexHintClause<C, IR, FT>, MySQLQuery._IndexPurposeClause<C, FT>
        , MySQLQuery._IntoSpec<C, Q> {

    private MySQLIndexHint.Command command;

    private List<String> intoVarList;

    private boolean fromOrCrossValid = true;

    MySQLSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final IR useIndex() {
        if (this.fromOrCrossValid) {
            this.command = MySQLIndexHint.Command.USE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        if (this.fromOrCrossValid) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        if (this.fromOrCrossValid) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (this.fromOrCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.USE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (this.fromOrCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (this.fromOrCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final FT useIndex(List<String> indexList) {
        if (this.fromOrCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.USE_INDEX, null, indexList);
        }
        return (FT) this;
    }

    @Override
    public final FT ignoreIndex(List<String> indexList) {
        if (this.fromOrCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexList);
        }
        return (FT) this;
    }

    @Override
    public final FT forceIndex(List<String> indexList) {
        if (this.fromOrCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexList);
        }
        return (FT) this;
    }

    @Override
    public final FT ifUseIndex(Function<C, List<String>> function) {
        if (this.fromOrCrossValid) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.USE_INDEX, null, list);
            }
        }
        return (FT) this;
    }

    @Override
    public final FT ifIgnoreIndex(Function<C, List<String>> function) {
        if (this.fromOrCrossValid) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, list);
            }
        }
        return (FT) this;
    }

    @Override
    public final FT ifForceIndex(Function<C, List<String>> function) {
        if (this.fromOrCrossValid) {
            final List<String> list;
            list = function.apply(this.criteria);
            if (list != null && list.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, null, list);
            }
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(List<String> indexList) {
        if (this.fromOrCrossValid) {
            final MySQLIndexHint.Command command = this.command;
            if (command != null) {
                this.command = null;//firstly clear command
                this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, indexList);

            }
        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(List<String> indexList) {
        if (this.fromOrCrossValid) {
            final MySQLIndexHint.Command command = this.command;
            if (command != null) {
                this.command = null;//firstly clear command
                this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, indexList);

            }
        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(List<String> indexList) {
        if (this.fromOrCrossValid) {
            final MySQLIndexHint.Command command = this.command;
            if (command != null) {
                this.command = null;//firstly clear command
                this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, indexList);
            }
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(Function<C, List<String>> function) {
        if (this.fromOrCrossValid && this.command != null) {
            this.forJoin(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(Function<C, List<String>> function) {
        if (this.fromOrCrossValid && this.command != null) {
            this.forOrderBy(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(Function<C, List<String>> function) {
        if (this.fromOrCrossValid && this.command != null) {
            this.forGroupBy(function.apply(this.criteria));
        }
        return (FT) this;
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
    final void crossJoinEvent(boolean success) {
        this.fromOrCrossValid = success;
    }

    @Override
    final List<MySQLWords> asModifierList(@Nullable List<MySQLWords> modifiers) {
        return MySQLUtils.asModifierList(this.criteriaContext, modifiers, MySQLUtils::selectModifier);
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return MySQLUtils.asHintList(this.criteriaContext, hints, MySQLHints::castHint);
    }

    /*################################## blow private method ##################################*/

    /**
     * @see #useIndex(List)
     * @see #ignoreIndex(List)
     * @see #forceIndex(List)
     * @see #forOrderBy(List)
     * @see #forGroupBy(List)
     * @see #forJoin(List)
     */
    private void addIndexHint(MySQLIndexHint.Command command, @Nullable MySQLIndexHint.Purpose purpose
            , @Nullable List<String> indexNames) {

        if (this.command != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final _TableBlock block = this.criteriaContext.lastTableBlockWithoutOnClause();
        if (!(block instanceof MySQLNoOnBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        if (indexNames == null || indexNames.size() == 0) {
            throw MySQLUtils.indexListIsEmpty();
        }
        final MySQLNoOnBlock tableBlock = (MySQLNoOnBlock) block;
        List<MySQLIndexHint> indexHintList = tableBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            tableBlock.indexHintList = indexHintList;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
    }


    enum MySQLLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        LOCK_IN_SHARE_MODE(_Constant.SPACE_LOCK_IN_SHARE_MODE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE);

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
