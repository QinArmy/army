package io.army.proxy;


import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.session.SessionFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Deprecated
final class SessionCacheFactory implements _SessionCacheFactory {

    static SessionCacheFactory create(SessionFactory sessionFactory) {
        return new SessionCacheFactory(sessionFactory);
    }


    private static final String ADVICE_FIELD_NAME = "army$_interceptor$$__";

    private static final String SETTER_METHOD_NAME = "army$_interceptor$$__";

    private final ConcurrentMap<Class<?>, Class<?>> proxyClassMap = new ConcurrentHashMap<>();

    final SessionFactory sessionFactory;

    final boolean uniqueCache;

    private final ByteBuddy byteBuddy;

    private SessionCacheFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.byteBuddy = new ByteBuddy();
        this.uniqueCache = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<? extends T> getProxyClass(final TableMeta<T> table) {
        return (Class<? extends T>) this.proxyClassMap.computeIfAbsent(table.javaType(), k -> createProxy(table));
    }

    @Override
    public _SessionCache createCache() {
        return new SessionCache(this);
    }


    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> createProxy(final TableMeta<T> table) {
        final Class<T> domainClass = table.javaType();

        String fieldName, setterName;
        ElementMatcher.Junction<?> junction = null;
        StringBuilder builder;
        int fieldNameLength;
        TableMeta<?> currentTable = table;

        while (currentTable != null) {
            for (FieldMeta<?> field : currentTable.fieldList()) {
                if (field instanceof PrimaryFieldMeta && currentTable instanceof ChildTableMeta) {
                    continue;
                }
                fieldName = field.fieldName();
                fieldNameLength = fieldName.length();
                builder = new StringBuilder(3 + fieldNameLength)
                        .append("set")
                        .append(Character.toUpperCase(fieldName.charAt(0)));
                if (fieldNameLength > 1) {
                    builder.append(fieldName.substring(1));
                }
                setterName = builder.toString();

                if (junction == null) {
                    junction = ElementMatchers.named(setterName)
                            .and(ElementMatchers.takesArguments(field.javaType()));
                } else {
                    junction = junction.or(
                            ElementMatchers.named(setterName)
                                    .and(ElementMatchers.takesArguments(field.javaType()))
                    );
                }

            }
            if (currentTable instanceof ChildTableMeta) {
                currentTable = ((ChildTableMeta<?>) currentTable).parentMeta();
            } else {
                currentTable = null;
            }
        }

        assert junction != null;
        return this.byteBuddy
                .subclass(table.javaType())
                .defineProperty(ADVICE_FIELD_NAME, Object.class)
                .implement(ArmyProxy.class)
                .method((ElementMatcher<? super MethodDescription>) junction)
                .intercept(MethodDelegation.to(SessionCache.ArmyBuddyInterceptor.INSTANCE, BuddyInterceptor.class))
                .make()
                .load(domainClass.getClassLoader())
                .getLoaded();
    }


}
