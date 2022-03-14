package io.army.cache;

import io.army.ErrorCode;
import io.army.annotation.UpdateMode;
import io.army.bean.DomainReadonlyWrapper;
import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner._Predicate;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Assert;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.*;

final class DomainSetterInterceptor implements MethodInterceptor, DomainUpdateAdvice {

    static DomainSetterInterceptor build(DomainReadonlyWrapper readonlyWrapper, DomainSetterPointcut pointcut) {
        _Assert.isTrue(readonlyWrapper.tableMeta() == pointcut.tableMeta()
                , () -> String.format("readonlyWrapper[%s] and pointcut[%s] not match."
                        , readonlyWrapper.tableMeta(), pointcut.tableMeta()));

        return new DomainSetterInterceptor(readonlyWrapper, createPredicateList(readonlyWrapper)
                , pointcut.setterFieldMap());
    }

    private static List<_Predicate> createPredicateList(DomainReadonlyWrapper readonlyWrapper) {
        TableMeta<?> tableMeta = readonlyWrapper.tableMeta();

        List<_Predicate> predicateList = new ArrayList<>(2);

        // 1. id predicate
        final PrimaryFieldMeta<?> idMeta = tableMeta.id();
        final Object idValue = readonlyWrapper.get(idMeta.fieldName());
        _Assert.notNull(idValue, "Domain Id is null");
       // predicateList.add(Predicates.primaryValueEquals(idMeta, idValue));

        FieldMeta<?> versionMeta = null;
        if (tableMeta instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            ParentTableMeta<?> parentMeta = childMeta.parentMeta();
            if (parentMeta.containField(_MetaBridge.VERSION)) {
                versionMeta = parentMeta.getField(_MetaBridge.VERSION);
            }
        } else if (tableMeta.containField(_MetaBridge.VERSION)) {
            versionMeta = tableMeta.getField(_MetaBridge.VERSION);
        }

        if (versionMeta != null) {
            // 2. version predicate
            Object versionValue = readonlyWrapper.get(versionMeta.fieldName());
            _Assert.notNull(versionValue, "Domain version is null");
            predicateList.add((_Predicate) versionMeta.equal(versionValue));
        }
        return predicateList;
    }


    private final DomainReadonlyWrapper readonlyWrapper;

    private final List<_Predicate> predicateList;

    private final Map<Method, FieldMeta<?>> setterFieldMap;

    private Set<FieldMeta<?>> targetFieldSet;

    private DomainSetterInterceptor(DomainReadonlyWrapper readonlyWrapper, List<_Predicate> predicateList
            , Map<Method, FieldMeta<?>> setterFieldMap) {
        this.readonlyWrapper = readonlyWrapper;
        this.predicateList = Collections.unmodifiableList(predicateList);
        this.setterFieldMap = setterFieldMap;
    }


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object targetObject = invocation.getThis();

        if (targetObject == null || targetObject.getClass() != this.readonlyWrapper.tableMeta().javaType()) {
            throw new DomainProxyException("Target Object[%s] isn't %s instance.", targetObject
                    , this.readonlyWrapper.tableMeta().javaType());
        }
        // 1. obtain target field meta
        final FieldMeta<?> fieldMeta = this.setterFieldMap.get(invocation.getMethod());
        if (fieldMeta == null) {
            throw new DomainProxyException("method[%s] not found FieldMeta.", invocation.getMethod());
        }
        if (fieldMeta.updateMode() == UpdateMode.IMMUTABLE) {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "FieldMeta[%s] not updatable.", fieldMeta);
        }
        //2. obtain old value
        final Object oldValue = this.readonlyWrapper.get(fieldMeta.fieldName());
        //3. invoke setter
        final Object result = invocation.proceed();
        //4. obtain new value
        Object newObject = this.readonlyWrapper.get(fieldMeta.fieldName());
        if (!Objects.equals(newObject, oldValue)) {
            if (this.targetFieldSet == null) {
                this.targetFieldSet = new HashSet<>();
            }
            // 5. add target field meta (optional)
            this.targetFieldSet.add(fieldMeta);
        }
        return result;
    }

    /*################################## blow DomainUpdateAdvice method ##################################*/

    @Override
    public void updateFinish() {
        if (this.targetFieldSet != null) {
            this.targetFieldSet.clear();
        }
    }

    @Override
    public DomainReadonlyWrapper readonlyWrapper() {
        return this.readonlyWrapper;
    }

    @Override
    public boolean hasUpdate() {
        return this.targetFieldSet != null
                && !this.targetFieldSet.isEmpty();
    }

    @Override
    public Set<FieldMeta<?>> targetFieldSet() {
        Set<FieldMeta<?>> set;
        if (this.targetFieldSet == null) {
            set = Collections.emptySet();
        } else {
            set = Collections.unmodifiableSet(this.targetFieldSet);
        }
        return set;
    }

    @Override
    public List<_Predicate> predicateList() {
        return this.predicateList;
    }
}
