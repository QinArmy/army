package io.army.proxy;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.IPredicate;
import io.army.criteria.ItemPair;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

final class SessionCache implements _SessionCache {

    private final SessionCacheFactory cacheFactory;

    private final boolean reactive;

    private final Map<TableMeta<?>, Map<Object, Wrapper>> cacheMap;


    SessionCache(SessionCacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
        if (cacheFactory.sessionFactory.isReactive()) {
            this.reactive = true;
            this.cacheMap = new ConcurrentHashMap<>();
        } else {
            this.reactive = false;
            this.cacheMap = new HashMap<>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T get(final TableMeta<T> table, final Object id) {
        if (!table.id().javaType().isInstance(id)) {
            String m = String.format("%s isn't %s type.", _ClassUtils.safeClassName(id), table);
            throw new IllegalArgumentException(m);
        }

        final Map<Object, Wrapper> domainMap;
        domainMap = this.cacheMap.get(table);
        final T domain;
        if (domainMap == null) {
            domain = null;
        } else {
            final Wrapper wrapper;
            wrapper = domainMap.get(id);
            if (wrapper == null) {
                domain = null;
            } else {
                domain = (T) wrapper.domain;
            }
        }
        return domain;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T get(final TableMeta<T> table, final UniqueFieldMeta<? super T> field
            , final Object fieldValue) {
        final TableMeta<? super T> belongTable = field.tableMeta();
        if (belongTable != table
                && table instanceof ChildTableMeta
                && belongTable != ((ChildTableMeta<?>) table).parentMeta()) {
            String m = String.format("%s and %s not match.", table, field);
            throw new IllegalArgumentException(m);
        }
        if (!field.javaType().isInstance(fieldValue)) {
            String m = String.format("%s isn't %s type.", _ClassUtils.safeClassName(fieldValue), table);
            throw new IllegalArgumentException(m);
        }

        final Map<Object, Wrapper> domainMap;
        domainMap = this.cacheMap.get(table);
        final T domain;
        final Wrapper w;
        if (domainMap == null) {
            domain = null;
        } else if ((w = domainMap.get(new UniqueKey(field, fieldValue))) == null) {
            domain = null;
        } else {
            domain = (T) w.domain;
        }
        return domain;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomain> T putIfAbsent(final TableMeta<T> table, final T domain) {
        final ObjectAccessor accessor;
        accessor = ObjectAccessorFactory.forBean(table.javaType());
        final Object id;
        id = accessor.get(domain, _MetaBridge.ID);
        if (id == null) {
            throw new IllegalArgumentException("domain id is null");
        }
        final Map<Object, Wrapper> wrapperMap;
        wrapperMap = this.cacheMap.computeIfAbsent(table, this::createCacheMap);
        final Wrapper w, old;
        w = createWrapper(accessor, domain);

        old = wrapperMap.putIfAbsent(id, w);
        final T result;
        if (old == null) {
            if (this.cacheFactory.uniqueCache) {
                doUniqueMapping(table, domain, wrapperMap, accessor, w);
            }
            result = (T) w.domain;
        } else {
            result = (T) old.domain;
        }
        return result;
    }

    @Override
    public List<_CacheBlock> getChangedList() {
        List<_CacheBlock> list = null;
        Map<String, Boolean> changedFieldMap;
        Wrapper w;
        for (Map.Entry<TableMeta<?>, Map<Object, Wrapper>> entry : this.cacheMap.entrySet()) {
            for (Map.Entry<Object, Wrapper> e : entry.getValue().entrySet()) {
                if (e.getKey() instanceof UniqueKey) {
                    continue;
                }
                w = e.getValue();
                changedFieldMap = w.changedMap;
                if (changedFieldMap.size() == 0) {
                    continue;
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(createCacheBlock(entry.getKey(), w));
            }
        }
        if (list == null) {
            list = Collections.emptyList();
        } else if (list.size() == 1) {
            list = Collections.singletonList(list.get(0));
        } else {
            list = Collections.unmodifiableList(list);
        }
        return list;
    }

    @Override
    public void clearChangedOnRollback() {
        for (Map<Object, Wrapper> map : cacheMap.values()) {
            for (Wrapper w : map.values()) {
                w.changedMap.clear();
            }
        }
    }

    @Override
    public void clearOnSessionCLose() {
        this.cacheMap.clear();
    }

    private _CacheBlock createCacheBlock(final TableMeta<?> table, final Wrapper w) {
        final Map<String, Boolean> changedFieldMap = w.changedMap;
        final ObjectAccessor accessor = w.accessor;
        final IDomain domain = w.domain;


        final Object id = accessor.get(domain, _MetaBridge.ID);
        if (id == null || !id.equals(w.id)) {
            throw _Exceptions.immutableField(table.id());
        }
        final ParentTableMeta<?> parent;
        if (table instanceof ChildTableMeta) {
            parent = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            parent = null;
        }
        final FieldMeta<?> versionField;
        versionField = table.version();
        final Number versionValue;
        final Function<Object, IPredicate> versionOperator;
        if (versionField == null) {
            versionValue = null;
            versionOperator = null;
        } else if ((versionValue = (Number) accessor.get(domain, _MetaBridge.VERSION)) == null) {
            throw _Exceptions.nonNullField(versionField);
        } else {
            versionOperator = versionField::equal;
        }

        final Consumer<Consumer<ItemPair>> pairConsumer = consumer -> {
            FieldMeta<?> field;
            for (String fieldName : changedFieldMap.keySet()) {
                if (table.containField(fieldName)) {
                    field = table.getField(fieldName);
                } else if (parent == null || !parent.containField(fieldName)) {
                    String m = String.format("Unknown field[%s] for %s", fieldName, table);
                    throw new IllegalStateException(m);
                } else {
                    field = parent.getField(fieldName);
                }
                consumer.accept(SQLs.itemPair(field, accessor.get(domain, fieldName)));
            }
        };

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(table, "t")
                .setPairs(pairConsumer)
                .where(table.id().equal(id))
                .ifNonNullAnd(versionOperator, versionValue)
                .asUpdate();
        return new CacheBlock(w, versionValue, stmt);
    }

    private <T extends IDomain> void doUniqueMapping(final TableMeta<T> table, final T domain
            , final Map<Object, Wrapper> wrapperMap, final ObjectAccessor accessor, final Wrapper w) {
        UniqueKey uniqueKey;
        Object value;
        TableMeta<?> current = table;
        while (true) {
            for (IndexMeta<?> index : current.indexList()) {
                for (IndexFieldMeta<?> indexField : index.fieldList()) {
                    if (indexField instanceof PrimaryFieldMeta
                            || !(indexField instanceof UniqueFieldMeta)) {
                        continue;
                    }
                    value = accessor.get(domain, indexField.fieldName());
                    if (value == null) {
                        continue;
                    }
                    uniqueKey = new UniqueKey((UniqueFieldMeta<?>) indexField, value);
                    wrapperMap.putIfAbsent(uniqueKey, w);
                }
            }
            if (!(current instanceof ChildTableMeta)) {
                break;
            }
            current = ((ChildTableMeta<?>) current).parentMeta();
        }

    }

    private Wrapper createWrapper(ObjectAccessor accessor, IDomain domain) {
        final Wrapper w;
        w = new Wrapper(accessor, domain, this.reactive);
        ((ArmyProxy) domain).setArmy$_interceptor$$__(w);
        return w;
    }

    private <T> Map<T, Wrapper> createCacheMap(final TableMeta<?> table) {
        final Map<T, Wrapper> map;
        if (this.reactive) {
            map = new ConcurrentHashMap<>();
        } else {
            map = new HashMap<>();
        }
        return map;
    }


    static final class ArmyBuddyInterceptor implements BuddyInterceptor {

        static final ArmyBuddyInterceptor INSTANCE = new ArmyBuddyInterceptor();

        private ArmyBuddyInterceptor() {
        }

        @Override
        public Object intercept(final Object instance, final Method method, final Callable<?> callable)
                throws Throwable {

            final Object callResult;
            callResult = callable.call();

            final Object wrapper;
            wrapper = ((ArmyProxy) instance).getArmy$_interceptor$$__();
            if (wrapper == null) {
                return callResult;
            }
            if (!(wrapper instanceof Wrapper) || ((Wrapper) wrapper).domain != instance) {
                throw new IllegalStateException("army proxy is changed.");
            }
            final String methodName;
            methodName = method.getName();
            final int length = methodName.length();
            final StringBuilder fieldNameBuilder = new StringBuilder(length - 3)
                    .append(Character.toLowerCase(methodName.charAt(3)));
            if (methodName.length() > 4) {
                fieldNameBuilder.append(methodName.substring(4));
            }
            ((Wrapper) wrapper).changedMap.putIfAbsent(fieldNameBuilder.toString(), Boolean.TRUE);
            return callResult;
        }


    }//ArmyBuddyInterceptor


    private static final class UniqueKey {

        private final UniqueFieldMeta<?> field;

        private final Object value;

        public UniqueKey(UniqueFieldMeta<?> field, Object value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.field, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof UniqueKey) {
                final UniqueKey key = (UniqueKey) obj;
                match = key.field == this.field && key.value.equals(this.value);
            } else {
                match = false;
            }
            return match;
        }

    }//UniqueKey


    private static final class Wrapper {

        private final Object id;

        private final ObjectAccessor accessor;

        private final IDomain domain;

        private final Map<String, Boolean> changedMap;

        private Wrapper(ObjectAccessor accessor, IDomain domain, boolean reactive) {
            this.id = accessor.get(domain, _MetaBridge.ID);
            assert this.id != null;
            this.accessor = accessor;
            this.domain = domain;
            if (reactive) {
                this.changedMap = new ConcurrentHashMap<>();
            } else {
                this.changedMap = new HashMap<>();
            }
        }

    }//Wrapper

    private static final class CacheBlock implements _CacheBlock {

        private final Wrapper w;

        private final Number version;

        private final Update stmt;


        private CacheBlock(Wrapper w, @Nullable Number version, Update stmt) {
            this.w = w;
            this.version = version;
            this.stmt = stmt;
        }

        @Override
        public Object id() {
            final Object id = this.w.id;
            assert id != null;
            return id;
        }

        @Override
        public Update statement() {
            return this.stmt;
        }

        @Override
        public void success() {
            this.w.changedMap.clear();
            final Number version = this.version;
            if (version == null) {
                return;
            }
            final Number newVersion;
            if (version instanceof Integer) {
                newVersion = version.intValue() + 1;
            } else if (version instanceof Long) {
                newVersion = version.longValue() + 1L;
            } else if (version instanceof BigInteger) {
                newVersion = ((BigInteger) version).add(BigInteger.ONE);
            } else {
                // FieldMeta no bug,never here.
                throw new IllegalStateException("error java type of version field.");
            }
            this.w.accessor.set(this.w.domain, _MetaBridge.VERSION, newVersion);
        }

    }//CacheBlock


}
