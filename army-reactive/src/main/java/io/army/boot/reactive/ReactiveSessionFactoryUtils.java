package io.army.boot.reactive;

import io.army.GenericSessionFactoryUtils;
import io.army.advice.GenericDomainAdvice;
import io.army.dialect.Database;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericReactiveApiSession;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.jdbd.DatabaseProductMetaData;
import io.jdbd.StatelessDatabaseSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

abstract class ReactiveSessionFactoryUtils extends GenericSessionFactoryUtils {

    static Mono<Database> queryDatabase(StatelessDatabaseSession session) {
        return session.getDatabaseMetaData()
                .getDatabaseProduct()
                .map(ReactiveSessionFactoryUtils::mapDatabase);
    }

    @SuppressWarnings("unchecked")
    static <T extends GenericDomainAdvice> Map<TableMeta<?>, T> createDomainInsertAdviceMap(
            @Nullable Collection<T> domainInsertAdvices, Class<T> adviceClass) {
        if (CollectionUtils.isEmpty(domainInsertAdvices)) {
            return Collections.emptyMap();
        }

        Map<TableMeta<?>, List<T>> map = new HashMap<>();

        for (T insertAdvice : domainInsertAdvices) {
            for (TableMeta<?> tableMeta : insertAdvice.supportTableMetaSet()) {
                List<T> list = map.computeIfAbsent(tableMeta, k -> new ArrayList<>(1));
                list.add(insertAdvice);
            }
        }

        Map<TableMeta<?>, T> adviceMap = new HashMap<>((int) (map.size() / 0.75F));
        for (Map.Entry<TableMeta<?>, List<T>> e : map.entrySet()) {
            List<T> list = e.getValue();
            if (list.size() == 1) {
                adviceMap.put(e.getKey(), list.get(0));
            } else if (adviceClass == ReactiveDomainInsertAdvice.class) {
                ReactiveDomainInsertAdvice insertAdvice;
                insertAdvice = ReactiveDomainInsertAdviceComposite.build(
                        e.getKey(), (List<ReactiveDomainInsertAdvice>) list);
                adviceMap.put(e.getKey(), adviceClass.cast(insertAdvice));
            } else if (adviceClass == ReactiveDomainUpdateAdvice.class) {
                ReactiveDomainUpdateAdvice updateAdvice;
                updateAdvice = ReactiveDomainUpdateAdviceComposite.build(e.getKey()
                        , (List<ReactiveDomainUpdateAdvice>) list);
                adviceMap.put(e.getKey(), adviceClass.cast(updateAdvice));
            } else if (adviceClass == ReactiveDomainDeleteAdvice.class) {
                ReactiveDomainDeleteAdvice deleteAdvice;
                deleteAdvice = ReactiveDomainDeleteAdviceComposite.build(e.getKey()
                        , (List<ReactiveDomainDeleteAdvice>) list);
                adviceMap.put(e.getKey(), adviceClass.cast(deleteAdvice));
            } else {
                throw new IllegalArgumentException("not support " + adviceClass.getName());
            }
        }
        return Collections.unmodifiableMap(adviceMap);
    }

    private static Database mapDatabase(DatabaseProductMetaData productMetaData) {
        return convertToDatabase(productMetaData.getProductName()
                , productMetaData.getMajorVersion()
                , productMetaData.getMinorVersion());
    }


    /*################################## blow static inner class ##################################*/

    private static final class ReactiveDomainInsertAdviceComposite implements ReactiveDomainInsertAdvice {

        private static ReactiveDomainInsertAdviceComposite build(TableMeta<?> tableMeta
                , List<ReactiveDomainInsertAdvice> domainInsertAdvices) {

            List<ReactiveDomainInsertAdvice> adviceList;
            adviceList = new ArrayList<>(domainInsertAdvices.size());
            adviceList.addAll(domainInsertAdvices);

            adviceList.sort(Comparator.comparingInt(ReactiveDomainInsertAdvice::order));

            return new ReactiveDomainInsertAdviceComposite(tableMeta, adviceList);
        }

        private final Set<TableMeta<?>> tableMetaSet;

        private final List<ReactiveDomainInsertAdvice> adviceList;

        private ReactiveDomainInsertAdviceComposite(TableMeta<?> tableMeta
                , List<ReactiveDomainInsertAdvice> adviceList) {
            Assert.notEmpty(adviceList, "adviceList must not empty.");
            this.tableMetaSet = Collections.singleton(tableMeta);
            this.adviceList = Collections.unmodifiableList(adviceList);
        }

        @Override
        public int order() {
            return this.adviceList.get(0).order();
        }

        @Override
        public Set<TableMeta<?>> supportTableMetaSet() {
            return this.tableMetaSet;
        }

        @Override
        public Mono<Void> beforeInsert(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeInsert(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterInsert(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterInsert(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> InsertThrows(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession, Throwable ex) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.InsertThrows(tableMeta, proxySession, ex))
                    .then()
                    ;
        }
    }

    private static final class ReactiveDomainUpdateAdviceComposite implements ReactiveDomainUpdateAdvice {

        private static ReactiveDomainUpdateAdvice build(TableMeta<?> tableMeta
                , List<ReactiveDomainUpdateAdvice> domainInsertAdvices) {

            List<ReactiveDomainUpdateAdvice> adviceList;
            adviceList = new ArrayList<>(domainInsertAdvices.size());
            adviceList.addAll(domainInsertAdvices);

            adviceList.sort(Comparator.comparingInt(ReactiveDomainUpdateAdvice::order));

            return new ReactiveDomainUpdateAdviceComposite(tableMeta, adviceList);
        }

        private final Set<TableMeta<?>> tableMetaSet;

        private final List<ReactiveDomainUpdateAdvice> adviceList;

        private ReactiveDomainUpdateAdviceComposite(TableMeta<?> tableMeta
                , List<ReactiveDomainUpdateAdvice> adviceList) {
            Assert.notEmpty(adviceList, "adviceList must not empty.");
            this.tableMetaSet = Collections.singleton(tableMeta);
            this.adviceList = Collections.unmodifiableList(adviceList);
        }

        @Override
        public int order() {
            return this.adviceList.get(0).order();
        }

        @Override
        public Set<TableMeta<?>> supportTableMetaSet() {
            return this.tableMetaSet;
        }

        @Override
        public Mono<Void> beforeUpdate(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeUpdate(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterUpdate(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterUpdate(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> updateThrows(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession, Throwable ex) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.updateThrows(tableMeta, proxySession, ex))
                    .then()
                    ;
        }
    }

    private static final class ReactiveDomainDeleteAdviceComposite implements ReactiveDomainDeleteAdvice {

        private static ReactiveDomainDeleteAdvice build(TableMeta<?> tableMeta
                , List<ReactiveDomainDeleteAdvice> domainInsertAdvices) {

            List<ReactiveDomainDeleteAdvice> adviceList;
            adviceList = new ArrayList<>(domainInsertAdvices.size());
            adviceList.addAll(domainInsertAdvices);

            adviceList.sort(Comparator.comparingInt(ReactiveDomainDeleteAdvice::order));

            return new ReactiveDomainDeleteAdviceComposite(tableMeta, adviceList);
        }

        private final Set<TableMeta<?>> tableMetaSet;

        private final List<ReactiveDomainDeleteAdvice> adviceList;

        private ReactiveDomainDeleteAdviceComposite(TableMeta<?> tableMeta
                , List<ReactiveDomainDeleteAdvice> adviceList) {
            Assert.notEmpty(adviceList, "adviceList must not empty.");
            this.tableMetaSet = Collections.singleton(tableMeta);
            this.adviceList = Collections.unmodifiableList(adviceList);
        }

        @Override
        public int order() {
            return this.adviceList.get(0).order();
        }

        @Override
        public Set<TableMeta<?>> supportTableMetaSet() {
            return this.tableMetaSet;
        }

        @Override
        public Mono<Void> beforeDelete(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeDelete(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterDelete(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterDelete(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> deleteThrows(TableMeta<?> tableMeta, GenericReactiveApiSession proxySession, Throwable ex) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.deleteThrows(tableMeta, proxySession, ex))
                    .then()
                    ;
        }
    }

}
