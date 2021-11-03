package io.army.boot.reactive;

import io.army.GenericRmSessionFactory;
import io.army.GenericSessionFactoryUtils;
import io.army.SessionFactoryException;
import io.army.advice.GenericDomainAdvice;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.reactive.GenericProxyReactiveSession;
import io.army.reactive.GenericReactiveApiSessionFactory;
import io.army.reactive.GenericReactiveSessionFactory;
import io.army.reactive.advice.ReactiveDomainDeleteAdvice;
import io.army.reactive.advice.ReactiveDomainInsertAdvice;
import io.army.reactive.advice.ReactiveDomainUpdateAdvice;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.ReflectionUtils;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.DatabaseSessionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

abstract class SessionFactoryUtils extends GenericSessionFactoryUtils {

    static DatabaseSessionFactory tryObtainPrimaryFactory(DatabaseSessionFactory factory) {
        DatabaseSessionFactory actualFactory;
//        if (factory instanceof ReadWriteSplittingSessionFactory) {
//            ReadWriteSplittingSessionFactory rwFactory = (ReadWriteSplittingSessionFactory) factory;
//            actualFactory = rwFactory.getPrimaryFactory();
//            if (actualFactory == null) {
//                throw new IllegalArgumentException(factory.getClass().getName() + ".getPrimaryFactory() return null.");
//            }
//        } else {
//            actualFactory = factory;
//        }
//        return actualFactory;
        return null;
    }

    static Mono<Database> queryDatabase(DatabaseSession session) {
//        return session.getDatabaseMetaData()
//                .getDatabaseProduct()
//                .map(SessionFactoryUtils::mapDatabase);
        return Mono.empty();
    }

    static Dialect createDialect(GenericRmSessionFactory rmSessionFactory, Database queriedDatabase) {
        Database configDatabase = readDatabase(rmSessionFactory);
        return createDialect(configDatabase, queriedDatabase, rmSessionFactory);
    }

    static void assertReactiveTableCountOfSharding(final int tableCountOfSharding
            , GenericReactiveSessionFactory sessionFactory) {
        assertTableCountOfSharding(tableCountOfSharding, sessionFactory);
    }

    static CurrentSessionContext createCurrentSessionContext(InnerReactiveApiSessionFactory sessionFactory) {
        final String className = "io.army.boot.reactive.SpringCurrentSessionContext";
        try {
            CurrentSessionContext sessionContext;
            if (sessionFactory.springApplication()) {
                // spring application environment
                Class<?> contextClass = Class.forName(className);
                Method method;
                method = ReflectionUtils.findMethod(contextClass, "build", GenericReactiveApiSessionFactory.class);
                if (method != null
                        && Modifier.isStatic(method.getModifiers())
                        && contextClass.isAssignableFrom(method.getReturnType())) {
                    sessionContext = (CurrentSessionContext) method.invoke(null, sessionFactory);
                } else {
                    throw new SessionFactoryException("%s definition error.", className);
                }
            } else {
                sessionContext = DefaultCurrentSessionContext.build(sessionFactory);
            }
            return sessionContext;
        } catch (ClassNotFoundException e) {
            throw new SessionFactoryException(e, "not found %s class", className);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new SessionFactoryException(e, e.getMessage());
        }
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

//    private static Database mapDatabase(DatabaseProductMetaData productMetaData) {
//        return convertToDatabase(productMetaData.getProductName()
//                , productMetaData.getMajorVersion()
//                , productMetaData.getMinorVersion());
//    }


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
        public Mono<Void> beforeInsert(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeInsert(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterInsert(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterInsert(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> InsertThrows(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession, Throwable ex) {
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
        public Mono<Void> beforeUpdate(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeUpdate(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterUpdate(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterUpdate(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> updateThrows(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession, Throwable ex) {
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
        public Mono<Void> beforeDelete(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.beforeDelete(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> afterDelete(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession) {
            if (!this.tableMetaSet.contains(tableMeta)) {
                return Mono.error(new IllegalArgumentException(String.format("not support %s", tableMeta)));
            }
            return Flux.fromIterable(this.adviceList)
                    .flatMap(advice -> advice.afterDelete(tableMeta, proxySession))
                    .then()
                    ;
        }

        @Override
        public Mono<Void> deleteThrows(TableMeta<?> tableMeta, GenericProxyReactiveSession proxySession, Throwable ex) {
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
