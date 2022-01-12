package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
abstract class MySQLIndexHintOnBlock<C, IR, IC, OR> extends OnClauseTableBlock<C, OR>
        implements MySQLQuery.IndexHintClause<C, IR, IC>, MySQLQuery.IndexPurposeClause<C, IC>
        , _MySQLTableBlock {

    private List<MySQLIndexHint> indexHintList;

    private MySQLIndexHint.Command indexHintCommand;

    MySQLIndexHintOnBlock(JoinType joinType, TableMeta<?> table, String alias) {
        super(joinType, table, alias);
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
        if (predicate.test(this.getCriteria())) {
            this.useIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.getCriteria())) {
            this.ignoreIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.getCriteria())) {
            this.forceIndex();
        } else {
            this.indexHintCommand = null;
        }
        return (IR) this;
    }

    @Override
    public final IC useIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, null, indexList);
        return (IC) this;
    }

    @Override
    public final IC ignoreIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, null, indexList);
        return (IC) this;
    }

    @Override
    public final IC forceIndex(List<String> indexList) {
        this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, null, indexList);
        return (IC) this;
    }

    @Override
    public final IC ifUseIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.getCriteria());
        if (!CollectionUtils.isEmpty(list)) {
            this.useIndex(list);
        }
        return (IC) this;
    }

    @Override
    public final IC ifIgnoreIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.getCriteria());
        if (!CollectionUtils.isEmpty(list)) {
            this.ignoreIndex(list);
        }
        return (IC) this;
    }

    @Override
    public final IC ifForceIndex(Function<C, List<String>> function) {
        final List<String> list;
        list = function.apply(this.getCriteria());
        if (!CollectionUtils.isEmpty(list)) {
            this.forceIndex(list);
        }
        return (IC) this;
    }


    @Override
    public final IC forJoin(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_JOIN, indexList);

        }
        return (IC) this;
    }

    @Override
    public final IC forOrderBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_ORDER_BY, indexList);

        }
        return (IC) this;
    }

    @Override
    public final IC forGroupBy(List<String> indexList) {
        final MySQLIndexHint.Command command = this.indexHintCommand;
        if (command != null) {
            this.indexHintCommand = null;//firstly clear command
            this.addIndexHint(command, MySQLIndexHint.Purpose.FOR_GROUP_BY, indexList);
        }
        return (IC) this;
    }

    @Override
    public final IC forJoin(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forJoin(function.apply(this.getCriteria()));
        }
        return (IC) this;
    }

    @Override
    public final IC forOrderBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forOrderBy(function.apply(this.getCriteria()));
        }
        return (IC) this;
    }

    @Override
    public final IC forGroupBy(Function<C, List<String>> function) {
        if (this.indexHintCommand != null) {
            this.forGroupBy(function.apply(this.getCriteria()));
        }
        return (IC) this;
    }


    @Override
    public final List<String> partitionList() {
        return Collections.emptyList();
    }

    @Override
    public final List<? extends _IndexHint> indexHintList() {
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = Collections.emptyList();
        } else {
            indexHintList = CollectionUtils.asUnmodifiableList(indexHintList);
        }
        return indexHintList;
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
        if (CollectionUtils.isEmpty(indexNames)) {
            throw new CriteriaException("index name list must not empty.");
        }
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.indexHintList = indexHintList;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
    }


}
