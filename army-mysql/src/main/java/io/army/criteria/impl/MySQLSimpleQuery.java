package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Query;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


@SuppressWarnings("unchecked")
abstract class MySQLSimpleQuery<C, Q extends Query, SR, FT, FS, FP, IR, JT, JS, JP, JC, JE, JF, WR, AR, GR, HR, OR, LR, UR, SP>
        extends SimpleQuery<C, Q, SR, FT, FS, FP, JT, JS, JP, JC, JE, JF, WR, AR, GR, HR, OR, LR, UR, SP>
        implements MySQLQuery, _MySQLQuery, MySQLQuery.MySQLJoinClause<C, JT, JS, JP, JC, FS, JE, JF>
        , MySQLQuery.MySQLFromClause<C, FT, FS, FP, JE>, MySQLQuery.IndexHintClause<C, IR, FT>
        , MySQLQuery.IndexPurposeClause<C, FT> {

    private MySQLIndexHint.Command indexHintCommand;


    MySQLSimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    public final FP from(TableMeta<?> table) {
        return this.createNoneBlockBeforeAs(table);
    }


    @Override
    public final IR useIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.USER_INDEX);
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.IGNORE_INDEX);
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        this.setIndexHintCommand(MySQLIndexHint.Command.FORCE_INDEX);
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.useIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.ignoreIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.forceIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final FT useIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT ignoreIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT forceIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexList);
        return (FT) this;
    }

    @Override
    public final FT ifUseIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
            this.useIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
            this.ignoreIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT ifForceIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.criteria);
        if (!_CollectionUtils.isEmpty(list)) {
            this.forceIndex(list);
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, indexList);

        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, indexList);

        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, indexList);
        }
        return (FT) this;
    }

    @Override
    public final FT forJoin(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forJoin(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forOrderBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forOrderBy(function.apply(this.criteria));
        }
        return (FT) this;
    }

    @Override
    public final FT forGroupBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forGroupBy(function.apply(this.criteria));
        }
        return (FT) this;
    }

    abstract JP createNoActionPartitionBlock();


    @Override
    final _TableBlock createNoneTableBlock(TableMeta<?> table, String tableAlias) {
        return new MySQLNoneBlock<>(table, tableAlias, this);
    }




    /*################################## blow private method ##################################*/


    @Override
    final JP ifJointTableBeforeAs(Predicate<C> predicate, _JoinType joinType, TableMeta<?> table) {
        final JP block;
        if (predicate.test(this.criteria)) {
            block = this.createBlockBeforeAs(joinType, table);
        } else {
            block = createNoActionPartitionBlock();
        }
        return block;
    }


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private void setIndexHintCommand(MySQLIndexHint.Command command) {
        if (this.indexHintCommand != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final _TableBlock block = this.criteriaContext.lastNoneBlock();
        if (!(block instanceof MySQLNoneBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        this.indexHintCommand = command;
    }

    /**
     * @see #useIndex(List)
     * @see #ignoreIndex(List)
     * @see #forceIndex(List)
     * @see #forOrderBy(List)
     * @see #forGroupBy(List)
     * @see #forJoin(List)
     */
    private void addIndexHint(MySQLIndexHint.Command command, @Nullable MySQLIndexHint.Purpose purpose
            , List<String> indexNames) {

        if (this.indexHintCommand != null) {
            throw _Exceptions.castCriteriaApi();
        }
        final _TableBlock block = this.criteriaContext.lastNoneBlock();
        if (!(block instanceof MySQLNoneBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        if (_CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index name list must not empty.");
        }
        final MySQLNoneBlock<C, OR> tableBlock = (MySQLNoneBlock<C, OR>) block;
        List<MySQLIndexHint> indexHintList = tableBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            tableBlock.indexHintList = indexHintList;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
    }


    enum MySQLLock implements SQLModifier {

        FOR_UPDATE(" FOR UPDATE"),
        LOCK_IN_SHARE_MODE(" LOCK IN SHARE MODE"),
        SHARE(" SHARE");

        final String words;

        MySQLLock(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


        @Override
        public final String toString() {
            return String.format("%s.%s", MySQLLock.class.getName(), this.name());
        }

    }//MySQLLock

    enum MySQLLockOption implements SQLModifier {

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
            return String.format("%s.%s", MySQLLockOption.class.getName(), this.name());
        }

    }//MySQLLockOption


}
