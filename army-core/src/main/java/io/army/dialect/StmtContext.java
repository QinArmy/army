package io.army.dialect;

import io.army.criteria.NamedParam;
import io.army.criteria.Visible;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.StrictParamValue;

import java.util.*;
import java.util.function.Function;

abstract class StmtContext implements _StmtContext {

    static final String SPACE_PLACEHOLDER = " ?";

    protected final ArmyDialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected StmtContext(ArmyDialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();
    }

    protected StmtContext(ArmyDialect dialect, boolean preferLiteral, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        if (preferLiteral) {
            this.paramList = this.createParamList();
        } else {
            this.paramList = new ArrayList<>();
        }
    }

    protected StmtContext(StmtContext outerContext) {
        this.dialect = outerContext.dialect;
        this.visible = outerContext.visible;
        this.sqlBuilder = outerContext.sqlBuilder;
        this.paramList = outerContext.paramList;
    }


    @Override
    public final _Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }


    @Override
    public final void appendParam(final ParamValue paramValue) {
        if (this instanceof _InsertBlock
                && !(paramValue instanceof StrictParamValue || paramValue instanceof NamedParam)) {
            this.valueInsertAppendParam(paramValue);
        } else {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            this.paramList.add(paramValue);
        }

    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


    List<ParamValue> createParamList() {
        return new ArrayList<>();
    }

    private void valueInsertAppendParam(final ParamValue paramValue) {
        final ParamMeta paramMeta = paramValue.paramMeta();
        if (paramMeta.mappingType() instanceof _ArmyNoInjectionMapping) {
            final Object value;
            value = paramValue.value();
            if (value == null) {
                this.sqlBuilder.append(_Constant.SPACE_NULL);
            } else {
                this.sqlBuilder.append(_Constant.SPACE)
                        .append(this.dialect.literal(paramMeta, value));
            }
        } else {
            this.sqlBuilder.append(SPACE_PLACEHOLDER);
            this.paramList.add(paramValue);
        }
    }


    static final class ProxyList implements List<ParamValue> {

        final List<ParamValue> paramList = new ArrayList<>();

        private final Function<NamedParam, ParamValue> function;

        ProxyList(Function<NamedParam, ParamValue> function) {
            this.function = function;
        }

        @Override
        public int size() {
            return this.paramList.size();
        }

        @Override
        public boolean isEmpty() {
            return this.paramList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.paramList.contains(o);
        }

        @Override
        public Iterator<ParamValue> iterator() {
            return this.paramList.iterator();
        }

        @Override
        public Object[] toArray() {
            return this.paramList.toArray();
        }

        @SuppressWarnings("all")
        @Override
        public <T> T[] toArray(T[] a) {
            return this.paramList.toArray(a);
        }

        @Override
        public boolean add(final ParamValue paramValue) {
            final ParamValue actual;
            if (paramValue instanceof NamedParam) {
                actual = this.function.apply((NamedParam) paramValue);
            } else {
                actual = paramValue;
            }
            return this.paramList.add(actual);
        }

        @Override
        public boolean remove(Object o) {
            return this.paramList.remove(o);
        }

        @SuppressWarnings("all")
        @Override
        public boolean containsAll(Collection<?> c) {
            return this.paramList.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends ParamValue> c) {
            return this.paramList.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends ParamValue> c) {
            return this.paramList.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.paramList.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.paramList.retainAll(c);
        }

        @Override
        public void clear() {
            this.paramList.clear();
        }

        @Override
        public ParamValue get(int index) {
            return this.paramList.get(index);
        }

        @Override
        public ParamValue set(int index, ParamValue element) {
            return this.paramList.set(index, element);
        }

        @Override
        public void add(int index, ParamValue element) {
            this.paramList.add(index, element);
        }

        @Override
        public ParamValue remove(int index) {
            return this.paramList.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.paramList.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.paramList.lastIndexOf(o);
        }

        @Override
        public ListIterator<ParamValue> listIterator() {
            return this.paramList.listIterator();
        }

        @Override
        public ListIterator<ParamValue> listIterator(int index) {
            return this.paramList.listIterator(index);
        }

        @Override
        public List<ParamValue> subList(int fromIndex, int toIndex) {
            return this.paramList.subList(fromIndex, toIndex);
        }

    }//ProxyList


}
